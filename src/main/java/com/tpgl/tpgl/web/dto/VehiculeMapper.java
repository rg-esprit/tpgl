package com.tpgl.tpgl.web.dto;

import com.tpgl.tpgl.domain.Equipement;
import com.tpgl.tpgl.domain.StatutVehicule;
import com.tpgl.tpgl.domain.Vehicule;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper dédié à la conversion entre l'entité {@link Vehicule} et {@link VehiculeDTO} (Figure 2).
 */
@Component
public class VehiculeMapper {

    public VehiculeDTO toDto(Vehicule vehicule) {
        if (vehicule == null) {
            return null;
        }

        List<EquipementDTO> equipementDtos = vehicule.getEquipements() != null
                ? vehicule.getEquipements().stream()
                .map(this::toEquipementDto)
                .collect(Collectors.toList())
                : Collections.emptyList();

        List<Long> equipementIds = vehicule.getEquipements() != null
                ? vehicule.getEquipements().stream()
                .map(Equipement::getId)
                .collect(Collectors.toList())
                : Collections.emptyList();

        return VehiculeDTO.builder()
                .id(vehicule.getId())
                .immatriculation(vehicule.getImmatriculation())
                .marque(vehicule.getMarque())
                .modele(vehicule.getModele())
                .categorie(vehicule.getCategorie())
                .tarifJournalier(vehicule.getTarifJournalier())
                .statut(vehicule.getStatut() != null ? vehicule.getStatut() : StatutVehicule.DISPONIBLE)
                .agenceId(vehicule.getAgence() != null ? vehicule.getAgence().getId() : null)
                .agenceNom(vehicule.getAgence() != null ? vehicule.getAgence().getNom() : null)
                .equipementIds(new ArrayList<>(equipementIds))
                .equipements(new ArrayList<>(equipementDtos))
                .build();
    }

    public Vehicule toEntity(VehiculeDTO dto) {
        if (dto == null) {
            return null;
        }

        return Vehicule.builder()
                .id(dto.getId())
                .immatriculation(dto.getImmatriculation())
                .marque(dto.getMarque())
                .modele(dto.getModele())
                .categorie(dto.getCategorie())
                .tarifJournalier(dto.getTarifJournalier())
                .statut(dto.getStatut() != null ? dto.getStatut() : StatutVehicule.DISPONIBLE)
                .build();
    }

    public EquipementDTO toEquipementDto(Equipement equipement) {
        if (equipement == null) {
            return null;
        }
        return EquipementDTO.builder()
                .id(equipement.getId())
                .libelle(equipement.getLibelle())
                .tarifJournalier(equipement.getTarifJournalier())
                .build();
    }

    public Equipement toEquipementEntity(EquipementDTO dto) {
        if (dto == null) {
            return null;
        }
        return Equipement.builder()
                .id(dto.getId())
                .libelle(dto.getLibelle())
                .tarifJournalier(dto.getTarifJournalier() != null ? dto.getTarifJournalier() : new java.math.BigDecimal("5.00"))
                .build();
    }
}
