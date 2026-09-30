package com.tpgl.tpgl.web.controller;

import com.tpgl.tpgl.aspect.RequiresRole;
import com.tpgl.tpgl.domain.RoleEmploye;
import com.tpgl.tpgl.service.IAgenceService;
import com.tpgl.tpgl.web.dto.AgenceDTO;
import com.tpgl.tpgl.web.dto.EmployeDTO;
import com.tpgl.tpgl.web.dto.EquipementDTO;
import com.tpgl.tpgl.web.dto.MaintenanceDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des agences, employés, équipements et maintenances.
 */
@RestController
@RequestMapping("/api/agences")
@RequiredArgsConstructor
@Tag(name = "Agences, Employés, Équipements & Maintenances", description = "Gestion des agences et des ressources associées")
public class AgenceController {

    private final IAgenceService agenceService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle agence")
    public ResponseEntity<AgenceDTO> creerAgence(@Valid @RequestBody AgenceDTO agenceDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agenceService.creerAgence(agenceDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour une agence")
    public ResponseEntity<AgenceDTO> mettreAJourAgence(
            @PathVariable Long id,
            @Valid @RequestBody AgenceDTO agenceDTO
    ) {
        return ResponseEntity.ok(agenceService.mettreAJourAgence(id, agenceDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter une agence par son identifiant")
    public ResponseEntity<AgenceDTO> getAgenceById(@PathVariable Long id) {
        return ResponseEntity.ok(agenceService.getAgenceById(id));
    }

    @GetMapping
    @Operation(summary = "Lister toutes les agences")
    public ResponseEntity<List<AgenceDTO>> getAllAgences() {
        return ResponseEntity.ok(agenceService.getAllAgences());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une agence (sans suppression en cascade des véhicules)")
    public ResponseEntity<Void> supprimerAgence(@PathVariable Long id) {
        agenceService.supprimerAgence(id);
        return ResponseEntity.noContent().build();
    }

    // --- Employés ---
    @PostMapping("/employes")
    @Operation(summary = "Créer un employé")
    @RequiresRole({RoleEmploye.MANAGER})
    public ResponseEntity<EmployeDTO> creerEmploye(@Valid @RequestBody EmployeDTO employeDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agenceService.creerEmploye(employeDTO));
    }

    @PutMapping("/{agenceId}/employes/{employeId}")
    @Operation(summary = "Affecter un employé à une agence")
    @RequiresRole({RoleEmploye.MANAGER})
    public ResponseEntity<EmployeDTO> affecterEmployeAAgence(
            @PathVariable Long agenceId,
            @PathVariable Long employeId
    ) {
        return ResponseEntity.ok(agenceService.affecterEmployeAAgence(employeId, agenceId));
    }

    @PutMapping("/employes/{employeId}/desaffecter")
    @Operation(summary = "Désaffecter un employé de son agence")
    @RequiresRole({RoleEmploye.MANAGER})
    public ResponseEntity<EmployeDTO> desaffecterEmployeDAgence(@PathVariable Long employeId) {
        return ResponseEntity.ok(agenceService.desaffecterEmployeDAgence(employeId));
    }

    @GetMapping("/{agenceId}/employes")
    @Operation(summary = "Lister les employés d'une agence")
    public ResponseEntity<List<EmployeDTO>> getEmployesByAgence(@PathVariable Long agenceId) {
        return ResponseEntity.ok(agenceService.getEmployesByAgence(agenceId));
    }

    // --- Équipements ---
    @PostMapping("/equipements")
    @Operation(summary = "Créer un équipement optionnel (GPS, siège bébé, coffre de toit...)")
    public ResponseEntity<EquipementDTO> creerEquipement(@Valid @RequestBody EquipementDTO equipementDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agenceService.creerEquipement(equipementDTO));
    }

    @GetMapping("/equipements")
    @Operation(summary = "Lister tous les équipements optionnels")
    public ResponseEntity<List<EquipementDTO>> getAllEquipements() {
        return ResponseEntity.ok(agenceService.getAllEquipements());
    }

    // --- Maintenances ---
    @PostMapping("/maintenances")
    @Operation(summary = "Planifier une maintenance sur un véhicule")
    @RequiresRole({RoleEmploye.AGENT, RoleEmploye.MANAGER})
    public ResponseEntity<MaintenanceDTO> planifierMaintenance(@Valid @RequestBody MaintenanceDTO maintenanceDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agenceService.planifierMaintenance(maintenanceDTO));
    }

    @PutMapping("/maintenances/{maintenanceId}/cloturer")
    @Operation(summary = "Clôturer une maintenance et remettre le véhicule en statut DISPONIBLE")
    @RequiresRole({RoleEmploye.AGENT, RoleEmploye.MANAGER})
    public ResponseEntity<MaintenanceDTO> cloturerMaintenance(@PathVariable Long maintenanceId) {
        return ResponseEntity.ok(agenceService.cloturerMaintenance(maintenanceId));
    }

    @GetMapping("/maintenances/vehicule/{vehiculeId}")
    @Operation(summary = "Lister les maintenances d'un véhicule")
    public ResponseEntity<List<MaintenanceDTO>> getMaintenancesByVehicule(@PathVariable Long vehiculeId) {
        return ResponseEntity.ok(agenceService.getMaintenancesByVehicule(vehiculeId));
    }
}
