package com.tpgl.tpgl.service;

import com.tpgl.tpgl.domain.*;
import com.tpgl.tpgl.exception.BusinessException;
import com.tpgl.tpgl.exception.ResourceNotFoundException;
import com.tpgl.tpgl.repository.*;
import com.tpgl.tpgl.web.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du service métier de gestion des agences, employés, équipements,
 * maintenances et tableaux de bord statistiques (Séances 8, 10 & 11).
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AgenceServiceImpl implements IAgenceService {

    private final IAgenceRepository agenceRepository;
    private final IEmployeRepository employeRepository;
    private final IEquipementRepository equipementRepository;
    private final IMaintenanceRepository maintenanceRepository;
    private final IVehiculeRepository vehiculeRepository;
    private final IClientRepository clientRepository;
    private final IReservationRepository reservationRepository;
    private final IContratRepository contratRepository;
    private final AutoLocMapper mapper;

    // =========================================================================
    // Gestion des Agences
    // =========================================================================

    @Override
    public AgenceDTO creerAgence(AgenceDTO agenceDTO) {
        agenceRepository.findByNomIgnoreCase(agenceDTO.getNom()).ifPresent(existing -> {
            throw new BusinessException("Une agence portant le nom '" + agenceDTO.getNom() + "' existe déjà.");
        });

        Agence agence = mapper.toAgenceEntity(agenceDTO);
        Agence saved = agenceRepository.save(agence);
        log.info("Agence créée : '{}' à {} (#{})", saved.getNom(), saved.getVille(), saved.getId());
        return mapper.toAgenceDto(saved);
    }

    @Override
    public AgenceDTO mettreAJourAgence(Long id, AgenceDTO agenceDTO) {
        Agence agence = getAgenceOrThrow(id);

        if (agenceDTO.getNom() != null
                && !agenceDTO.getNom().equalsIgnoreCase(agence.getNom())
                && agenceRepository.findByNomIgnoreCase(agenceDTO.getNom()).isPresent()) {
            throw new BusinessException("Une autre agence porte déjà le nom '" + agenceDTO.getNom() + "'.");
        }

        if (agenceDTO.getNom() != null) {
            agence.setNom(agenceDTO.getNom());
        }
        if (agenceDTO.getVille() != null) {
            agence.setVille(agenceDTO.getVille());
        }
        if (agenceDTO.getAdresse() != null) {
            agence.setAdresse(agenceDTO.getAdresse());
        }
        if (agenceDTO.getTelephone() != null) {
            agence.setTelephone(agenceDTO.getTelephone());
        }

        return mapper.toAgenceDto(agenceRepository.save(agence));
    }

    @Override
    @Transactional(readOnly = true)
    public AgenceDTO getAgenceById(Long id) {
        return mapper.toAgenceDto(getAgenceOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgenceDTO> getAllAgences() {
        return agenceRepository.findAll().stream()
                .map(mapper::toAgenceDto)
                .collect(Collectors.toList());
    }

    @Override
    public void supprimerAgence(Long id) {
        Agence agence = getAgenceOrThrow(id);
        new ArrayList<>(agence.getVehicules()).forEach(agence::removeVehicule);
        new ArrayList<>(agence.getEmployes()).forEach(agence::removeEmploye);
        agenceRepository.delete(agence);
        log.info("Agence #{} supprimée (véhicules et employés détachés sans suppression en cascade).", id);
    }

    // =========================================================================
    // Gestion des Employés (Séance 8)
    // =========================================================================

    @Override
    public EmployeDTO creerEmploye(EmployeDTO employeDTO) {
        Employe employe = mapper.toEmployeEntity(employeDTO);
        if (employeDTO.getAgenceId() != null) {
            Agence agence = getAgenceOrThrow(employeDTO.getAgenceId());
            agence.addEmploye(employe);
        }
        Employe saved = employeRepository.save(employe);
        log.info("Employé créé : {} {} ({})", saved.getPrenom(), saved.getNom(), saved.getRole());
        return mapper.toEmployeDto(saved);
    }

    @Override
    public EmployeDTO affecterEmployeAAgence(Long employeId, Long agenceId) {
        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employe", "id", employeId));
        Agence agence = getAgenceOrThrow(agenceId);

        if (employe.getAgence() != null && !employe.getAgence().equals(agence)) {
            employe.getAgence().removeEmploye(employe);
        }
        agence.addEmploye(employe);

        Employe saved = employeRepository.save(employe);
        log.info("Employé {} {} affecté à l'agence '{}'.", saved.getPrenom(), saved.getNom(), agence.getNom());
        return mapper.toEmployeDto(saved);
    }

    @Override
    public EmployeDTO desaffecterEmployeDAgence(Long employeId) {
        Employe employe = employeRepository.findById(employeId)
                .orElseThrow(() -> new ResourceNotFoundException("Employe", "id", employeId));

        if (employe.getAgence() != null) {
            employe.getAgence().removeEmploye(employe);
        } else {
            employe.setAgence(null);
        }

        Employe saved = employeRepository.save(employe);
        log.info("Employé {} {} désaffecté de son agence.", saved.getPrenom(), saved.getNom());
        return mapper.toEmployeDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeDTO> getEmployesByAgence(Long agenceId) {
        getAgenceOrThrow(agenceId);
        return employeRepository.findByAgenceId(agenceId).stream()
                .map(mapper::toEmployeDto)
                .collect(Collectors.toList());
    }

    // =========================================================================
    // Gestion des Équipements
    // =========================================================================

    @Override
    public EquipementDTO creerEquipement(EquipementDTO equipementDTO) {
        if (equipementRepository.existsByLibelleIgnoreCase(equipementDTO.getLibelle())) {
            throw new BusinessException("Un équipement avec le libellé '" + equipementDTO.getLibelle() + "' existe déjà.");
        }
        Equipement equipement = mapper.toEquipementEntity(equipementDTO);
        Equipement saved = equipementRepository.save(equipement);
        log.info("Équipement créé : '{}' ({} €/j)", saved.getLibelle(), saved.getTarifJournalier());
        return mapper.toEquipementDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EquipementDTO> getAllEquipements() {
        return equipementRepository.findAll().stream()
                .map(mapper::toEquipementDto)
                .collect(Collectors.toList());
    }

    // =========================================================================
    // Gestion des Maintenances
    // =========================================================================

    @Override
    public MaintenanceDTO planifierMaintenance(MaintenanceDTO dto) {
        Vehicule vehicule = vehiculeRepository.findById(dto.getVehiculeId())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicule", "id", dto.getVehiculeId()));

        if (vehicule.getStatut() == StatutVehicule.LOUE) {
            throw new BusinessException("Impossible de placer en maintenance un véhicule actuellement loué.");
        }

        LocalDate debut = dto.getDateDebut() != null ? dto.getDateDebut() : LocalDate.now();
        if (dto.getDateFin() != null && dto.getDateFin().isBefore(debut)) {
            throw new BusinessException("La date de fin de maintenance ne peut pas être antérieure à la date de début.");
        }

        Maintenance maintenance = Maintenance.builder()
                .dateDebut(debut)
                .dateFin(dto.getDateFin())
                .description(dto.getDescription())
                .vehicule(vehicule)
                .build();

        vehicule.setStatut(StatutVehicule.MAINTENANCE);
        vehicule.addMaintenance(maintenance);

        Maintenance saved = maintenanceRepository.save(maintenance);
        vehiculeRepository.save(vehicule);

        log.info("Maintenance #{} planifiée pour le véhicule {} -> statut passé à MAINTENANCE.",
                saved.getId(), vehicule.getImmatriculation());
        return mapper.toMaintenanceDto(saved);
    }

    @Override
    public MaintenanceDTO cloturerMaintenance(Long maintenanceId) {
        Maintenance maintenance = maintenanceRepository.findById(maintenanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Maintenance", "id", maintenanceId));

        if (maintenance.getDateFin() == null) {
            maintenance.setDateFin(LocalDate.now());
        }

        Vehicule vehicule = maintenance.getVehicule();
        if (vehicule != null && vehicule.getStatut() == StatutVehicule.MAINTENANCE) {
            vehicule.setStatut(StatutVehicule.DISPONIBLE);
            vehiculeRepository.save(vehicule);
        }

        Maintenance saved = maintenanceRepository.save(maintenance);
        log.info("Maintenance #{} clôturée -> véhicule {} remis à DISPONIBLE.",
                saved.getId(), vehicule != null ? vehicule.getImmatriculation() : "N/A");
        return mapper.toMaintenanceDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaintenanceDTO> getMaintenancesByVehicule(Long vehiculeId) {
        if (!vehiculeRepository.existsById(vehiculeId)) {
            throw new ResourceNotFoundException("Vehicule", "id", vehiculeId);
        }
        return maintenanceRepository.findByVehiculeId(vehiculeId).stream()
                .map(mapper::toMaintenanceDto)
                .collect(Collectors.toList());
    }

    // =========================================================================
    // Reporting & Statistiques (Séances 10 & 11)
    // =========================================================================

    @Override
    @Transactional(readOnly = true)
    public StatistiqueAgenceDTO getStatistiquesAgence(Long agenceId) {
        Agence agence = getAgenceOrThrow(agenceId);

        long totalVehicules = vehiculeRepository.countByAgenceId(agenceId);
        long disponibles = vehiculeRepository.countByAgenceIdAndStatut(agenceId, StatutVehicule.DISPONIBLE);
        long loues = vehiculeRepository.countByAgenceIdAndStatut(agenceId, StatutVehicule.LOUE);
        long maintenance = vehiculeRepository.countByAgenceIdAndStatut(agenceId, StatutVehicule.MAINTENANCE);

        double tauxOccupation = totalVehicules > 0
                ? BigDecimal.valueOf((double) loues * 100.0 / totalVehicules)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue()
                : 0.0;

        long totalReservations = reservationRepository.countByAgenceId(agenceId);
        BigDecimal chiffreAffaires = contratRepository.sumChiffreAffairesByAgence(agenceId);
        if (chiffreAffaires == null) {
            chiffreAffaires = BigDecimal.ZERO;
        }

        return StatistiqueAgenceDTO.builder()
                .agenceId(agence.getId())
                .agenceNom(agence.getNom())
                .ville(agence.getVille())
                .totalVehicules(totalVehicules)
                .vehiculesDisponibles(disponibles)
                .vehiculesLoues(loues)
                .vehiculesEnMaintenance(maintenance)
                .tauxOccupationPourcent(tauxOccupation)
                .totalReservations(totalReservations)
                .chiffreAffairesTotal(chiffreAffaires)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public StatistiqueGlobaleDTO getStatistiquesGlobales() {
        List<Agence> agences = agenceRepository.findAll();
        List<StatistiqueAgenceDTO> statsParAgence = agences.stream()
                .map(a -> getStatistiquesAgence(a.getId()))
                .collect(Collectors.toList());

        long totalAgences = agences.size();
        long totalVehicules = vehiculeRepository.count();
        long totalLoues = vehiculeRepository.findByStatut(StatutVehicule.LOUE).size();
        long totalClients = clientRepository.count();
        long totalReservations = reservationRepository.count();
        long totalContratsValides = contratRepository.findByValideTrue().size();

        double tauxGlobal = totalVehicules > 0
                ? BigDecimal.valueOf((double) totalLoues * 100.0 / totalVehicules)
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue()
                : 0.0;

        BigDecimal caGlobal = contratRepository.sumChiffreAffairesGlobal();
        if (caGlobal == null) {
            caGlobal = BigDecimal.ZERO;
        }

        return StatistiqueGlobaleDTO.builder()
                .totalAgences(totalAgences)
                .totalVehicules(totalVehicules)
                .totalClients(totalClients)
                .totalReservations(totalReservations)
                .totalContratsValides(totalContratsValides)
                .tauxOccupationGlobalPourcent(tauxGlobal)
                .chiffreAffairesGlobal(caGlobal)
                .statistiquesParAgence(statsParAgence)
                .build();
    }

    private Agence getAgenceOrThrow(Long id) {
        return agenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Agence", "id", id));
    }
}
