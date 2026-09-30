package com.tpgl.tpgl.service;

import com.tpgl.tpgl.web.dto.*;

import java.util.List;

/**
 * Interface du service métier de gestion des agences, employés, équipements, maintenances et statistiques.
 */
public interface IAgenceService {

    // Agences
    AgenceDTO creerAgence(AgenceDTO agenceDTO);

    AgenceDTO mettreAJourAgence(Long id, AgenceDTO agenceDTO);

    AgenceDTO getAgenceById(Long id);

    List<AgenceDTO> getAllAgences();

    void supprimerAgence(Long id);

    // Employés (Séance 8 - Affectation / Désaffectation)
    EmployeDTO creerEmploye(EmployeDTO employeDTO);

    EmployeDTO affecterEmployeAAgence(Long employeId, Long agenceId);

    EmployeDTO desaffecterEmployeDAgence(Long employeId);

    List<EmployeDTO> getEmployesByAgence(Long agenceId);

    // Équipements
    EquipementDTO creerEquipement(EquipementDTO equipementDTO);

    List<EquipementDTO> getAllEquipements();

    // Maintenances
    MaintenanceDTO planifierMaintenance(MaintenanceDTO dto);

    MaintenanceDTO cloturerMaintenance(Long maintenanceId);

    List<MaintenanceDTO> getMaintenancesByVehicule(Long vehiculeId);

    // Reporting & Statistiques (Séances 10 & 11)
    StatistiqueAgenceDTO getStatistiquesAgence(Long agenceId);

    StatistiqueGlobaleDTO getStatistiquesGlobales();
}
