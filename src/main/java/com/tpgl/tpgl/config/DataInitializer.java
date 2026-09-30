package com.tpgl.tpgl.config;

import com.tpgl.tpgl.domain.*;
import com.tpgl.tpgl.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Initialisation automatique du jeu de données de démonstration AutoLoc dans PostgreSQL.
 */
@Component
@ConditionalOnProperty(name = "autoloc.seed.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final IAgenceRepository agenceRepository;
    private final IEmployeRepository employeRepository;
    private final IEquipementRepository equipementRepository;
    private final IVehiculeRepository vehiculeRepository;
    private final IClientRepository clientRepository;
    private final IReservationRepository reservationRepository;
    private final IContratRepository contratRepository;
    private final IPaiementRepository paiementRepository;
    private final IMaintenanceRepository maintenanceRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (agenceRepository.count() > 0) {
            log.info("[AutoLoc Seed] Base de données déjà initialisée ({} agences présentes).", agenceRepository.count());
            return;
        }

        log.info("[AutoLoc Seed] Initialisation du jeu de données AutoLoc dans PostgreSQL...");

        // 1. Agences
        Agence agenceTunis = agenceRepository.save(Agence.builder()
                .nom("AutoLoc Tunis Centre")
                .ville("Tunis")
                .adresse("Avenue Habib Bourguiba, Tunis")
                .telephone("+216 71 100 200")
                .build());

        Agence agenceSousse = agenceRepository.save(Agence.builder()
                .nom("AutoLoc Sousse Médina")
                .ville("Sousse")
                .adresse("Boulevard du 14 Janvier, Sousse")
                .telephone("+216 73 200 300")
                .build());

        // 2. Employés
        employeRepository.saveAll(List.of(
                Employe.builder().nom("Ben Ali").prenom("Sami").role(RoleEmploye.MANAGER).agence(agenceTunis).build(),
                Employe.builder().nom("Trabelsi").prenom("Ines").role(RoleEmploye.AGENT).agence(agenceTunis).build(),
                Employe.builder().nom("Gharbi").prenom("Karim").role(RoleEmploye.MANAGER).agence(agenceSousse).build()
        ));

        // 3. Équipements optionnels
        Equipement gps = equipementRepository.save(Equipement.builder()
                .libelle("GPS")
                .tarifJournalier(new BigDecimal("10.00"))
                .build());
        Equipement siegeBebe = equipementRepository.save(Equipement.builder()
                .libelle("Siège bébé")
                .tarifJournalier(new BigDecimal("8.00"))
                .build());
        Equipement coffreToit = equipementRepository.save(Equipement.builder()
                .libelle("Coffre de toit")
                .tarifJournalier(new BigDecimal("15.00"))
                .build());

        // 4. Véhicules
        Vehicule v1 = Vehicule.builder()
                .immatriculation("210-TN-1001")
                .marque("Renault")
                .modele("Clio 5")
                .categorie(CategorieVehicule.CITADINE)
                .tarifJournalier(new BigDecimal("80.00"))
                .statut(StatutVehicule.DISPONIBLE)
                .agence(agenceTunis)
                .build();
        v1.addEquipement(gps);
        v1 = vehiculeRepository.save(v1);

        Vehicule v2 = Vehicule.builder()
                .immatriculation("215-TN-2002")
                .marque("Peugeot")
                .modele("3008")
                .categorie(CategorieVehicule.SUV)
                .tarifJournalier(new BigDecimal("160.00"))
                .statut(StatutVehicule.LOUE)
                .agence(agenceTunis)
                .build();
        v2.addEquipement(gps);
        v2.addEquipement(siegeBebe);
        v2 = vehiculeRepository.save(v2);

        Vehicule v3 = Vehicule.builder()
                .immatriculation("220-TN-3003")
                .marque("BMW")
                .modele("Série 3")
                .categorie(CategorieVehicule.BERLINE)
                .tarifJournalier(new BigDecimal("220.00"))
                .statut(StatutVehicule.DISPONIBLE)
                .agence(agenceSousse)
                .build();
        v3.addEquipement(coffreToit);
        v3 = vehiculeRepository.save(v3);

        Vehicule v4 = Vehicule.builder()
                .immatriculation("225-TN-4004")
                .marque("Renault")
                .modele("Kangoo")
                .categorie(CategorieVehicule.UTILITAIRE)
                .tarifJournalier(new BigDecimal("110.00"))
                .statut(StatutVehicule.MAINTENANCE)
                .agence(agenceSousse)
                .build();
        v4 = vehiculeRepository.save(v4);

        // 5. Maintenance
        maintenanceRepository.save(Maintenance.builder()
                .dateDebut(LocalDate.now().minusDays(2))
                .dateFin(LocalDate.now().plusDays(3))
                .description("Révision complète et changement des plaquettes de frein")
                .vehicule(v4)
                .build());

        // 6. Clients
        Client client1 = clientRepository.save(Client.builder()
                .nom("Mansouri")
                .prenom("Yassine")
                .email("yassine.mansouri@example.com")
                .telephone("+216 55 111 222")
                .numPermis("PERM-TN-987654")
                .dateInscription(LocalDate.now().minusMonths(3))
                .build());

        Client client2 = clientRepository.save(Client.builder()
                .nom("Jaziri")
                .prenom("Amira")
                .email("amira.jaziri@example.com")
                .telephone("+216 22 333 444")
                .numPermis("PERM-TN-123456")
                .dateInscription(LocalDate.now().minusMonths(1))
                .build());

        // 7. Réservation confirmée + Contrat + Paiement
        Reservation res1 = reservationRepository.save(Reservation.builder()
                .dateDebut(LocalDate.now().minusDays(1))
                .dateFin(LocalDate.now().plusDays(2))
                .statut(StatutReservation.CONFIRMEE)
                .client(client1)
                .vehicule(v2)
                .build());

        Contrat contrat1 = contratRepository.save(Contrat.builder()
                .dateSignature(LocalDate.now().minusDays(1))
                .montantTotal(new BigDecimal("712.00"))
                .valide(true)
                .reservation(res1)
                .build());

        paiementRepository.save(Paiement.builder()
                .montant(new BigDecimal("300.00"))
                .datePaiement(LocalDate.now().minusDays(1))
                .modePaiement(ModePaiement.CARTE)
                .contrat(contrat1)
                .build());

        // 8. Réservation en attente
        reservationRepository.save(Reservation.builder()
                .dateDebut(LocalDate.now().plusDays(5))
                .dateFin(LocalDate.now().plusDays(8))
                .statut(StatutReservation.EN_ATTENTE)
                .client(client2)
                .vehicule(v1)
                .build());

        log.info("[AutoLoc Seed] Jeu de données initialisé avec succès.");
    }
}
