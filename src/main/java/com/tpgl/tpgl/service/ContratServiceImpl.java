package com.tpgl.tpgl.service;

import com.tpgl.tpgl.domain.*;
import com.tpgl.tpgl.exception.BusinessException;
import com.tpgl.tpgl.exception.ResourceNotFoundException;
import com.tpgl.tpgl.repository.IContratRepository;
import com.tpgl.tpgl.repository.IPaiementRepository;
import com.tpgl.tpgl.repository.IReservationRepository;
import com.tpgl.tpgl.web.dto.AutoLocMapper;
import com.tpgl.tpgl.web.dto.ContratDTO;
import com.tpgl.tpgl.web.dto.PaiementDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implémentation du service métier de gestion des contrats et des paiements (Séances 8 & 9).
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ContratServiceImpl implements IContratService {

    private final IContratRepository contratRepository;
    private final IReservationRepository reservationRepository;
    private final IPaiementRepository paiementRepository;
    private final ITarificationService tarificationService;
    private final AutoLocMapper mapper;

    @Override
    public ContratDTO etablirContrat(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "id", reservationId));

        if (reservation.getStatut() == StatutReservation.ANNULEE) {
            throw new BusinessException("Impossible d'établir un contrat pour une réservation annulée.");
        }

        Optional<Contrat> existing = contratRepository.findByReservationId(reservationId);
        if (existing.isPresent()) {
            Contrat c = existing.get();
            BigDecimal paye = paiementRepository.sumMontantByContratId(c.getId());
            return mapper.toContratDto(c, paye);
        }

        BigDecimal montantTotal = tarificationService.calculerTarifReservation(
                reservation.getVehicule(),
                reservation.getDateDebut(),
                reservation.getDateFin()
        );

        if (reservation.getStatut() == StatutReservation.EN_ATTENTE) {
            reservation.setStatut(StatutReservation.CONFIRMEE);
            if (reservation.getVehicule() != null) {
                reservation.getVehicule().setStatut(StatutVehicule.LOUE);
            }
        }

        Contrat contrat = Contrat.builder()
                .dateSignature(LocalDate.now())
                .montantTotal(montantTotal)
                .valide(true)
                .reservation(reservation)
                .build();

        Contrat saved = contratRepository.save(contrat);
        reservation.setContrat(saved);
        reservationRepository.save(reservation);

        log.info("Contrat #{} établi pour la réservation #{} (montant total : {} €).",
                saved.getId(), reservationId, montantTotal);
        return mapper.toContratDto(saved, BigDecimal.ZERO);
    }

    @Override
    public PaiementDTO enregistrerPaiement(Long contratId, PaiementDTO paiementDTO) {
        Contrat contrat = getOrThrow(contratId);

        if (!contrat.isValide()) {
            throw new BusinessException("Impossible d'enregistrer un paiement sur un contrat invalidé.");
        }
        if (paiementDTO.getMontant() == null || paiementDTO.getMontant().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Le montant du paiement doit être strictement positif.");
        }

        BigDecimal dejaPaye = paiementRepository.sumMontantByContratId(contratId);
        BigDecimal resteAPayer = contrat.getMontantTotal().subtract(dejaPaye);
        if (paiementDTO.getMontant().compareTo(resteAPayer) > 0) {
            throw new BusinessException(String.format(
                    "Le montant du paiement (%s €) dépasse le reste à payer (%s €) du contrat #%d.",
                    paiementDTO.getMontant(), resteAPayer, contratId));
        }

        Paiement paiement = Paiement.builder()
                .montant(paiementDTO.getMontant())
                .datePaiement(paiementDTO.getDatePaiement() != null ? paiementDTO.getDatePaiement() : LocalDate.now())
                .modePaiement(paiementDTO.getModePaiement() != null ? paiementDTO.getModePaiement() : ModePaiement.CARTE)
                .contrat(contrat)
                .build();

        contrat.addPaiement(paiement);
        Paiement saved = paiementRepository.save(paiement);

        log.info("Paiement #{} de {} € ({}) enregistré pour le contrat #{}.",
                saved.getId(), saved.getMontant(), saved.getModePaiement(), contratId);
        return mapper.toPaiementDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ContratDTO getContratById(Long contratId) {
        Contrat contrat = getOrThrow(contratId);
        BigDecimal paye = paiementRepository.sumMontantByContratId(contratId);
        return mapper.toContratDto(contrat, paye);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContratDTO> getAllContrats() {
        return contratRepository.findAll().stream()
                .map(c -> mapper.toContratDto(c, paiementRepository.sumMontantByContratId(c.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaiementDTO> getPaiementsByContrat(Long contratId) {
        getOrThrow(contratId);
        return paiementRepository.findByContratId(contratId).stream()
                .map(mapper::toPaiementDto)
                .collect(Collectors.toList());
    }

    @Override
    public void supprimerContrat(Long contratId) {
        Contrat contrat = getOrThrow(contratId);
        Reservation reservation = contrat.getReservation();
        if (reservation != null) {
            reservation.setContrat(null);
            reservationRepository.save(reservation);
        }
        contratRepository.delete(contrat);
        log.info("Contrat #{} supprimé (avec suppression en cascade de ses paiements associés).", contratId);
    }

    private Contrat getOrThrow(Long id) {
        return contratRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contrat", "id", id));
    }
}
