package com.tpgl.tpgl.scheduler;

import com.tpgl.tpgl.domain.Contrat;
import com.tpgl.tpgl.domain.Reservation;
import com.tpgl.tpgl.domain.StatutReservation;
import com.tpgl.tpgl.domain.StatutVehicule;
import com.tpgl.tpgl.domain.Vehicule;
import com.tpgl.tpgl.repository.IContratRepository;
import com.tpgl.tpgl.repository.IReservationRepository;
import com.tpgl.tpgl.repository.IVehiculeRepository;
import com.tpgl.tpgl.service.pattern.AutoLocConfigManager;
import com.tpgl.tpgl.service.pattern.ReservationEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Planificateur de tâches automatisées (Séance 11 - Spring Scheduler).
 * Gère la libération automatique des véhicules en fin de location, les alertes d'échéance
 * de contrats et le calcul périodique des indicateurs de la flotte AutoLoc.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ContratEcheanceScheduler {

    private final IReservationRepository reservationRepository;
    private final IContratRepository contratRepository;
    private final IVehiculeRepository vehiculeRepository;
    private final ReservationEventPublisher eventPublisher;

    /**
     * Tâche planifiée tous les jours à minuit (00:00).
     * Recherche les réservations confirmées dont la date de fin est dépassée,
     * passe leur statut à TERMINEE et remet le véhicule associé à DISPONIBLE.
     *
     * @return le nombre de réservations clôturées et de véhicules libérés
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public int libererVehiculesEnFinDeLocation() {
        LocalDate today = LocalDate.now();
        List<Reservation> expiredReservations = reservationRepository.findExpiredReservations(
                StatutReservation.CONFIRMEE, today
        );

        int count = 0;
        for (Reservation reservation : expiredReservations) {
            StatutReservation ancienStatut = reservation.getStatut();
            reservation.setStatut(StatutReservation.TERMINEE);
            reservationRepository.save(reservation);

            Vehicule vehicule = reservation.getVehicule();
            if (vehicule != null) {
                vehicule.setStatut(StatutVehicule.DISPONIBLE);
                vehiculeRepository.save(vehicule);
            }

            if (eventPublisher != null) {
                eventPublisher.notifyStatusChanged(reservation, ancienStatut, StatutReservation.TERMINEE);
            }
            count++;
        }

        log.info("[SCHEDULER] Libération automatique terminée : {} véhicule(s) remis à DISPONIBLE.", count);
        return count;
    }

    /**
     * Tâche planifiée tous les jours à 08:00.
     * Détecte les contrats actifs arrivant à échéance dans les prochains jours (par défaut 3 jours)
     * et génère des alertes dans les journaux.
     *
     * @return la liste des contrats arrivant prochainement à échéance
     */
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional(readOnly = true)
    public List<Contrat> alerterContratsArrivantAEcheance() {
        int delaiJours = AutoLocConfigManager.getInstance().getDelaiAlerteEcheanceJours();
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(delaiJours);

        List<Contrat> contratsProchesEcheance = contratRepository.findContractsNearingExpiration(startDate, endDate);
        for (Contrat contrat : contratsProchesEcheance) {
            Reservation res = contrat.getReservation();
            log.warn("[SCHEDULER-ALERTE] Contrat #{} arrive à échéance le {} (Client: {}, Véhicule: {})",
                    contrat.getId(),
                    res != null ? res.getDateFin() : "N/A",
                    (res != null && res.getClient() != null) ? res.getClient().getEmail() : "N/A",
                    (res != null && res.getVehicule() != null) ? res.getVehicule().getImmatriculation() : "N/A");
        }

        log.info("[SCHEDULER] Vérification des échéances ({} -> {}) : {} contrat(s) détecté(s).",
                startDate, endDate, contratsProchesEcheance.size());
        return contratsProchesEcheance;
    }

    /**
     * Tâche planifiée tous les jours à 23:30.
     * Calcule et journalise le taux d'occupation de la flotte et le chiffre d'affaires global.
     */
    @Scheduled(cron = "0 30 23 * * *")
    @Transactional(readOnly = true)
    public void calculerStatistiquesPeriodiques() {
        long totalVehicules = vehiculeRepository.count();
        long vehiculesLoues = vehiculeRepository.findByStatut(StatutVehicule.LOUE).size();
        long vehiculesDisponibles = vehiculeRepository.findByStatut(StatutVehicule.DISPONIBLE).size();
        BigDecimal chiffreAffairesGlobal = contratRepository.sumChiffreAffairesGlobal();
        String devise = AutoLocConfigManager.getInstance().getDevise();

        double tauxOccupation = totalVehicules > 0
                ? (vehiculesLoues * 100.0) / totalVehicules
                : 0.0;

        log.info("[SCHEDULER-STATS] Flotte totale: {} | Loués: {} | Disponibles: {} | Occupation: {}% | CA Global: {} {}",
                totalVehicules,
                vehiculesLoues,
                vehiculesDisponibles,
                String.format("%.2f", tauxOccupation),
                chiffreAffairesGlobal,
                devise);
    }
}
