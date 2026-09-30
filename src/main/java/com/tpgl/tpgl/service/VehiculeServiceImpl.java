package com.tpgl.tpgl.service;

import com.tpgl.tpgl.domain.*;
import com.tpgl.tpgl.exception.BusinessException;
import com.tpgl.tpgl.exception.ResourceNotFoundException;
import com.tpgl.tpgl.repository.IAgenceRepository;
import com.tpgl.tpgl.repository.IEquipementRepository;
import com.tpgl.tpgl.repository.IVehiculeRepository;
import com.tpgl.tpgl.repository.VehiculeSpecifications;
import com.tpgl.tpgl.web.dto.VehiculeDTO;
import com.tpgl.tpgl.web.dto.VehiculeMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du service métier de gestion des véhicules (Figure 2 & Séance 8).
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class VehiculeServiceImpl implements IVehiculeService {

    private final IVehiculeRepository vehiculeRepository;
    private final IAgenceRepository agenceRepository;
    private final IEquipementRepository equipementRepository;
    private final VehiculeMapper vehiculeMapper;

    @Override
    public VehiculeDTO creerVehicule(VehiculeDTO vehiculeDTO) {
        if (vehiculeRepository.existsByImmatriculation(vehiculeDTO.getImmatriculation())) {
            throw new BusinessException("Un véhicule avec l'immatriculation '" + vehiculeDTO.getImmatriculation() + "' existe déjà.");
        }

        Vehicule vehicule = vehiculeMapper.toEntity(vehiculeDTO);

        if (vehiculeDTO.getAgenceId() != null) {
            Agence agence = agenceRepository.findById(vehiculeDTO.getAgenceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agence", "id", vehiculeDTO.getAgenceId()));
            agence.addVehicule(vehicule);
        }

        if (vehiculeDTO.getEquipementIds() != null && !vehiculeDTO.getEquipementIds().isEmpty()) {
            for (Long equipementId : vehiculeDTO.getEquipementIds()) {
                Equipement equipement = equipementRepository.findById(equipementId)
                        .orElseThrow(() -> new ResourceNotFoundException("Equipement", "id", equipementId));
                vehicule.addEquipement(equipement);
            }
        }

        Vehicule saved = vehiculeRepository.save(vehicule);
        log.info("Véhicule créé avec succès : {} ({})", saved.getImmatriculation(), saved.getId());
        return vehiculeMapper.toDto(saved);
    }

    @Override
    public VehiculeDTO mettreAJourVehicule(Long id, VehiculeDTO vehiculeDTO) {
        Vehicule vehicule = getOrThrow(id);

        if (vehiculeDTO.getImmatriculation() != null
                && !vehiculeDTO.getImmatriculation().equalsIgnoreCase(vehicule.getImmatriculation())
                && vehiculeRepository.existsByImmatriculation(vehiculeDTO.getImmatriculation())) {
            throw new BusinessException("Un autre véhicule possède déjà l'immatriculation '" + vehiculeDTO.getImmatriculation() + "'.");
        }

        if (vehiculeDTO.getImmatriculation() != null) {
            vehicule.setImmatriculation(vehiculeDTO.getImmatriculation());
        }
        if (vehiculeDTO.getMarque() != null) {
            vehicule.setMarque(vehiculeDTO.getMarque());
        }
        if (vehiculeDTO.getModele() != null) {
            vehicule.setModele(vehiculeDTO.getModele());
        }
        if (vehiculeDTO.getCategorie() != null) {
            vehicule.setCategorie(vehiculeDTO.getCategorie());
        }
        if (vehiculeDTO.getTarifJournalier() != null) {
            vehicule.setTarifJournalier(vehiculeDTO.getTarifJournalier());
        }
        if (vehiculeDTO.getStatut() != null) {
            vehicule.setStatut(vehiculeDTO.getStatut());
        }

        if (vehiculeDTO.getAgenceId() != null) {
            Agence agence = agenceRepository.findById(vehiculeDTO.getAgenceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Agence", "id", vehiculeDTO.getAgenceId()));
            agence.addVehicule(vehicule);
        }

        Vehicule saved = vehiculeRepository.save(vehicule);
        return vehiculeMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public VehiculeDTO getVehiculeById(Long id) {
        return vehiculeMapper.toDto(getOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeDTO> getAllVehicules() {
        return vehiculeRepository.findAll().stream()
                .map(vehiculeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehiculeDTO> getVehiculesParPage(Pageable pageable) {
        return vehiculeRepository.findAll(pageable)
                .map(vehiculeMapper::toDto);
    }

    @Override
    public void supprimerVehicule(Long id) {
        Vehicule vehicule = getOrThrow(id);
        if (vehicule.getStatut() == StatutVehicule.LOUE) {
            throw new BusinessException("Impossible de supprimer un véhicule actuellement en cours de location.");
        }
        if (vehicule.getAgence() != null) {
            vehicule.getAgence().removeVehicule(vehicule);
        }
        new ArrayList<>(vehicule.getEquipements()).forEach(vehicule::removeEquipement);
        vehiculeRepository.delete(vehicule);
        log.info("Véhicule #{} supprimé.", id);
    }

    @Override
    public VehiculeDTO affecterVehiculeAAgence(Long vehiculeId, Long agenceId) {
        Vehicule vehicule = getOrThrow(vehiculeId);
        Agence agence = agenceRepository.findById(agenceId)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", "id", agenceId));

        if (vehicule.getAgence() != null && !vehicule.getAgence().equals(agence)) {
            vehicule.getAgence().removeVehicule(vehicule);
        }
        agence.addVehicule(vehicule);

        Vehicule saved = vehiculeRepository.save(vehicule);
        log.info("Véhicule {} affecté à l'agence '{}'.", vehicule.getImmatriculation(), agence.getNom());
        return vehiculeMapper.toDto(saved);
    }

    @Override
    public VehiculeDTO desaffecterVehiculeDAgence(Long vehiculeId) {
        Vehicule vehicule = getOrThrow(vehiculeId);
        Agence ancienneAgence = vehicule.getAgence();
        if (ancienneAgence != null) {
            ancienneAgence.removeVehicule(vehicule);
        } else {
            vehicule.setAgence(null);
        }

        Vehicule saved = vehiculeRepository.save(vehicule);
        log.info("Véhicule {} désaffecté de son agence.", vehicule.getImmatriculation());
        return vehiculeMapper.toDto(saved);
    }

    @Override
    public VehiculeDTO affecterEquipementAVehicule(Long vehiculeId, Long equipementId) {
        Vehicule vehicule = getOrThrow(vehiculeId);
        Equipement equipement = equipementRepository.findById(equipementId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipement", "id", equipementId));

        vehicule.addEquipement(equipement);
        Vehicule saved = vehiculeRepository.save(vehicule);
        log.info("Équipement '{}' affecté au véhicule {}.", equipement.getLibelle(), vehicule.getImmatriculation());
        return vehiculeMapper.toDto(saved);
    }

    @Override
    public VehiculeDTO desaffecterEquipementDeVehicule(Long vehiculeId, Long equipementId) {
        Vehicule vehicule = getOrThrow(vehiculeId);
        Equipement equipement = equipementRepository.findById(equipementId)
                .orElseThrow(() -> new ResourceNotFoundException("Equipement", "id", equipementId));

        vehicule.removeEquipement(equipement);
        Vehicule saved = vehiculeRepository.save(vehicule);
        log.info("Équipement '{}' désaffecté du véhicule {}.", equipement.getLibelle(), vehicule.getImmatriculation());
        return vehiculeMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeDTO> rechercherVehiculesDisponibles(
            String ville,
            CategorieVehicule categorie,
            LocalDate dateDebut,
            LocalDate dateFin
    ) {
        LocalDate debut = dateDebut != null ? dateDebut : LocalDate.now();
        LocalDate fin = dateFin != null ? dateFin : debut;

        if (fin.isBefore(debut)) {
            throw new BusinessException("La date de fin de recherche ne peut pas être antérieure à la date de début.");
        }

        String villeNormalized = (ville != null && !ville.isBlank()) ? ville.trim() : null;
        List<StatutReservation> statutsActifs = List.of(StatutReservation.EN_ATTENTE, StatutReservation.CONFIRMEE);

        return vehiculeRepository.findAvailableVehicles(villeNormalized, categorie, debut, fin, statutsActifs)
                .stream()
                .map(vehiculeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehiculeDTO> filtrerVehicules(
            String ville,
            CategorieVehicule categorie,
            StatutVehicule statut,
            BigDecimal maxTarif
    ) {
        Specification<Vehicule> spec = Specification
                .where(VehiculeSpecifications.hasVille(ville))
                .and(VehiculeSpecifications.hasCategorie(categorie))
                .and(VehiculeSpecifications.hasStatut(statut))
                .and(VehiculeSpecifications.hasMaxTarif(maxTarif));

        return vehiculeRepository.findAll(spec)
                .stream()
                .map(vehiculeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public VehiculeDTO changerStatutVehicule(Long vehiculeId, StatutVehicule statut) {
        if (statut == null) {
            throw new BusinessException("Le nouveau statut du véhicule ne peut pas être nul.");
        }
        Vehicule vehicule = getOrThrow(vehiculeId);
        vehicule.setStatut(statut);
        return vehiculeMapper.toDto(vehiculeRepository.save(vehicule));
    }

    private Vehicule getOrThrow(Long id) {
        return vehiculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", "id", id));
    }
}
