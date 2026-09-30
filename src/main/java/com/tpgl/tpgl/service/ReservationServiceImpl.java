package com.tpgl.tpgl.service;

import com.tpgl.tpgl.domain.*;
import com.tpgl.tpgl.exception.BusinessException;
import com.tpgl.tpgl.exception.ResourceNotFoundException;
import com.tpgl.tpgl.repository.IClientRepository;
import com.tpgl.tpgl.repository.IContratRepository;
import com.tpgl.tpgl.repository.IReservationRepository;
import com.tpgl.tpgl.repository.IVehiculeRepository;
import com.tpgl.tpgl.service.pattern.ReservationEventPublisher;
import com.tpgl.tpgl.web.dto.AutoLocMapper;
import com.tpgl.tpgl.web.dto.ReservationRequestDTO;
import com.tpgl.tpgl.web.dto.ReservationResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du service métier de réservation AutoLoc (Séances 8, 9 & 11).
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceImpl implements IReservationService {

    private final IReservationRepository reservationRepository;
    private final IClientRepository clientRepository;
    private final IVehiculeRepository vehiculeRepository;
    private final IContratRepository contratRepository;
    private final ITarificationService tarificationService;
    private final ReservationEventPublisher eventPublisher;
    private final AutoLocMapper mapper;

    @Override
    public ReservationResponseDTO creerReservation(ReservationRequestDTO request) {
        if (request.getDateDebut() == null || request.getDateFin() == null) {
            throw new BusinessException("Les dates de début et de fin de réservation sont obligatoires.");
        }
        if (request.getDateFin().isBefore(request.getDateDebut())) {
            throw new BusinessException("La date de fin doit être supérieure ou égale à la date de début.");
        }

        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", request.getClientId()));

        Vehicule vehicule = vehiculeRepository.findById(request.getVehiculeId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", "id", request.getVehiculeId()));

        if (vehicule.getStatut() != StatutVehicule.DISPONIBLE) {
            throw new BusinessException("Le véhicule " + vehicule.getImmatriculation()
                    + " n'est pas disponible (statut actuel : " + vehicule.getStatut() + ").");
        }

        List<StatutReservation> activeStatuses = List.of(StatutReservation.EN_ATTENTE, StatutReservation.CONFIRMEE);
        boolean overlap = reservationRepository.existsOverlappingReservation(
                vehicule.getId(),
                request.getDateDebut(),
                request.getDateFin(),
                activeStatuses
        );
        if (overlap) {
            throw new BusinessException("Le véhicule " + vehicule.getImmatriculation()
                    + " fait déjà l'objet d'une réservation sur la période demandée.");
        }

        Reservation reservation = Reservation.builder()
                .dateDebut(request.getDateDebut())
                .dateFin(request.getDateFin())
                .statut(StatutReservation.EN_ATTENTE)
                .client(client)
                .vehicule(vehicule)
                .build();

        Reservation saved = reservationRepository.save(reservation);
        BigDecimal montantEstime = tarificationService.calculerTarifReservation(
                vehicule,
                saved.getDateDebut(),
                saved.getDateFin()
        );

        log.info("Réservation #{} créée en statut EN_ATTENTE pour le véhicule {} (montant estimé : {} €).",
                saved.getId(), vehicule.getImmatriculation(), montantEstime);

        return mapper.toReservationResponseDto(saved, montantEstime);
    }

    @Override
    public ReservationResponseDTO validerReservation(Long reservationId) {
        Reservation reservation = getOrThrow(reservationId);

        if (reservation.getStatut() == StatutReservation.ANNULEE || reservation.getStatut() == StatutReservation.TERMINEE) {
            throw new BusinessException("Impossible de valider une réservation ayant le statut " + reservation.getStatut() + ".");
        }
        if (reservation.getStatut() == StatutReservation.CONFIRMEE && reservation.getContrat() != null) {
            return mapper.toReservationResponseDto(reservation, reservation.getContrat().getMontantTotal());
        }

        Vehicule vehicule = reservation.getVehicule();
        if (vehicule.getStatut() == StatutVehicule.MAINTENANCE) {
            throw new BusinessException("Impossible de valider la réservation : le véhicule est en maintenance.");
        }

        StatutReservation ancienStatut = reservation.getStatut();
        reservation.setStatut(StatutReservation.CONFIRMEE);
        vehicule.setStatut(StatutVehicule.LOUE);
        vehiculeRepository.save(vehicule);

        BigDecimal montantTotal = tarificationService.calculerTarifReservation(
                vehicule,
                reservation.getDateDebut(),
                reservation.getDateFin()
        );

        Contrat contrat = reservation.getContrat();
        if (contrat == null) {
            contrat = contratRepository.findByReservationId(reservation.getId()).orElse(null);
        }
        if (contrat == null) {
            contrat = Contrat.builder()
                    .dateSignature(LocalDate.now())
                    .montantTotal(montantTotal)
                    .valide(true)
                    .reservation(reservation)
                    .build();
            contrat = contratRepository.save(contrat);
            reservation.setContrat(contrat);
        } else {
            contrat.setMontantTotal(montantTotal);
            contrat.setValide(true);
            contratRepository.save(contrat);
        }

        Reservation saved = reservationRepository.save(reservation);
        eventPublisher.notifyStatusChanged(saved, ancienStatut, StatutReservation.CONFIRMEE);

        log.info("Réservation #{} validée -> Contrat #{} généré (montant : {} €).",
                saved.getId(), contrat.getId(), montantTotal);
        return mapper.toReservationResponseDto(saved, montantTotal);
    }

    @Override
    public ReservationResponseDTO annulerReservation(Long reservationId) {
        Reservation reservation = getOrThrow(reservationId);

        if (reservation.getStatut() == StatutReservation.TERMINEE) {
            throw new BusinessException("Impossible d'annuler une réservation déjà terminée.");
        }
        if (reservation.getStatut() == StatutReservation.ANNULEE) {
            throw new BusinessException("La réservation #" + reservationId + " est déjà annulée.");
        }

        StatutReservation ancienStatut = reservation.getStatut();
        reservation.setStatut(StatutReservation.ANNULEE);

        Vehicule vehicule = reservation.getVehicule();
        if (vehicule != null && vehicule.getStatut() == StatutVehicule.LOUE) {
            vehicule.setStatut(StatutVehicule.DISPONIBLE);
            vehiculeRepository.save(vehicule);
        }

        Contrat contrat = reservation.getContrat();
        if (contrat == null) {
            contrat = contratRepository.findByReservationId(reservation.getId()).orElse(null);
        }
        if (contrat != null) {
            contrat.setValide(false);
            contratRepository.save(contrat);
        }

        Reservation saved = reservationRepository.save(reservation);
        eventPublisher.notifyStatusChanged(saved, ancienStatut, StatutReservation.ANNULEE);

        BigDecimal montantEstime = tarificationService.calculerTarifReservation(
                vehicule,
                saved.getDateDebut(),
                saved.getDateFin()
        );
        log.info("Réservation #{} annulée.", saved.getId());
        return mapper.toReservationResponseDto(saved, montantEstime);
    }

    @Override
    public ReservationResponseDTO terminerReservation(Long reservationId) {
        Reservation reservation = getOrThrow(reservationId);

        if (reservation.getStatut() != StatutReservation.CONFIRMEE) {
            throw new BusinessException("Seule une réservation confirmée peut être clôturée/terminée (statut actuel : "
                    + reservation.getStatut() + ").");
        }

        StatutReservation ancienStatut = reservation.getStatut();
        reservation.setStatut(StatutReservation.TERMINEE);

        Vehicule vehicule = reservation.getVehicule();
        if (vehicule != null) {
            vehicule.setStatut(StatutVehicule.DISPONIBLE);
            vehiculeRepository.save(vehicule);
        }

        Reservation saved = reservationRepository.save(reservation);
        eventPublisher.notifyStatusChanged(saved, ancienStatut, StatutReservation.TERMINEE);

        BigDecimal montant = saved.getContrat() != null
                ? saved.getContrat().getMontantTotal()
                : tarificationService.calculerTarifReservation(vehicule, saved.getDateDebut(), saved.getDateFin());

        log.info("Réservation #{} terminée et véhicule {} remis à DISPONIBLE.",
                saved.getId(), vehicule != null ? vehicule.getImmatriculation() : "N/A");
        return mapper.toReservationResponseDto(saved, montant);
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponseDTO getReservationById(Long id) {
        Reservation reservation = getOrThrow(id);
        return toResponseWithTarif(reservation);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::toResponseWithTarif)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> getReservationsByClient(Long clientId) {
        if (!clientRepository.existsById(clientId)) {
            throw new ResourceNotFoundException("Client", "id", clientId);
        }
        return reservationRepository.findByClientId(clientId).stream()
                .map(this::toResponseWithTarif)
                .collect(Collectors.toList());
    }

    private Reservation getOrThrow(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", id));
    }

    private ReservationResponseDTO toResponseWithTarif(Reservation reservation) {
        BigDecimal montant = reservation.getContrat() != null
                ? reservation.getContrat().getMontantTotal()
                : tarificationService.calculerTarifReservation(
                        reservation.getVehicule(),
                        reservation.getDateDebut(),
                        reservation.getDateFin()
                );
        return mapper.toReservationResponseDto(reservation, montant);
    }
}
