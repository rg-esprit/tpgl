package com.tpgl.tpgl.web.controller;

import com.tpgl.tpgl.aspect.RequiresRole;
import com.tpgl.tpgl.domain.CategorieVehicule;
import com.tpgl.tpgl.domain.RoleEmploye;
import com.tpgl.tpgl.domain.StatutVehicule;
import com.tpgl.tpgl.service.IVehiculeService;
import com.tpgl.tpgl.web.dto.VehiculeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Contrôleur REST pour la gestion des véhicules et de la flotte (Séances 7, 8, 9, 10).
 */
@RestController
@RequestMapping("/api/vehicules")
@RequiredArgsConstructor
@Tag(name = "Véhicules", description = "Gestion de la flotte de véhicules, disponibilité et affectations")
public class VehiculeController {

    private final IVehiculeService vehiculeService;

    @PostMapping
    @Operation(summary = "Créer un nouveau véhicule")
    @RequiresRole({RoleEmploye.AGENT, RoleEmploye.MANAGER})
    public ResponseEntity<VehiculeDTO> creerVehicule(@Valid @RequestBody VehiculeDTO vehiculeDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculeService.creerVehicule(vehiculeDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un véhicule existant")
    @RequiresRole({RoleEmploye.AGENT, RoleEmploye.MANAGER})
    public ResponseEntity<VehiculeDTO> mettreAJourVehicule(
            @PathVariable Long id,
            @Valid @RequestBody VehiculeDTO vehiculeDTO
    ) {
        return ResponseEntity.ok(vehiculeService.mettreAJourVehicule(id, vehiculeDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un véhicule par son identifiant")
    public ResponseEntity<VehiculeDTO> getVehiculeById(@PathVariable Long id) {
        return ResponseEntity.ok(vehiculeService.getVehiculeById(id));
    }

    @GetMapping
    @Operation(summary = "Lister tous les véhicules")
    public ResponseEntity<List<VehiculeDTO>> getAllVehicules() {
        return ResponseEntity.ok(vehiculeService.getAllVehicules());
    }

    @GetMapping("/page")
    @Operation(summary = "Lister les véhicules avec pagination")
    public ResponseEntity<Page<VehiculeDTO>> getVehiculesParPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(vehiculeService.getVehiculesParPage(PageRequest.of(page, size)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un véhicule")
    @RequiresRole({RoleEmploye.MANAGER})
    public ResponseEntity<Void> supprimerVehicule(@PathVariable Long id) {
        vehiculeService.supprimerVehicule(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{vehiculeId}/affecter-agence/{agenceId}")
    @Operation(summary = "Affecter un véhicule à une agence (Séance 8)")
    @RequiresRole({RoleEmploye.AGENT, RoleEmploye.MANAGER})
    public ResponseEntity<VehiculeDTO> affecterVehiculeAAgence(
            @PathVariable Long vehiculeId,
            @PathVariable Long agenceId
    ) {
        return ResponseEntity.ok(vehiculeService.affecterVehiculeAAgence(vehiculeId, agenceId));
    }

    @PutMapping("/{vehiculeId}/desaffecter-agence")
    @Operation(summary = "Désaffecter un véhicule de son agence (Séance 8)")
    @RequiresRole({RoleEmploye.AGENT, RoleEmploye.MANAGER})
    public ResponseEntity<VehiculeDTO> desaffecterVehiculeDAgence(@PathVariable Long vehiculeId) {
        return ResponseEntity.ok(vehiculeService.desaffecterVehiculeDAgence(vehiculeId));
    }

    @PutMapping("/{vehiculeId}/affecter-equipement/{equipementId}")
    @Operation(summary = "Affecter un équipement optionnel à un véhicule (Séance 8)")
    @RequiresRole({RoleEmploye.AGENT, RoleEmploye.MANAGER})
    public ResponseEntity<VehiculeDTO> affecterEquipementAVehicule(
            @PathVariable Long vehiculeId,
            @PathVariable Long equipementId
    ) {
        return ResponseEntity.ok(vehiculeService.affecterEquipementAVehicule(vehiculeId, equipementId));
    }

    @PutMapping("/{vehiculeId}/desaffecter-equipement/{equipementId}")
    @Operation(summary = "Désaffecter un équipement d'un véhicule (Séance 8)")
    @RequiresRole({RoleEmploye.AGENT, RoleEmploye.MANAGER})
    public ResponseEntity<VehiculeDTO> desaffecterEquipementDeVehicule(
            @PathVariable Long vehiculeId,
            @PathVariable Long equipementId
    ) {
        return ResponseEntity.ok(vehiculeService.desaffecterEquipementDeVehicule(vehiculeId, equipementId));
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Rechercher les véhicules disponibles par ville, catégorie et période (Séance 10)")
    public ResponseEntity<List<VehiculeDTO>> rechercherVehiculesDisponibles(
            @RequestParam(required = false) String ville,
            @RequestParam(required = false) CategorieVehicule categorie,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateDebut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFin
    ) {
        return ResponseEntity.ok(vehiculeService.rechercherVehiculesDisponibles(ville, categorie, dateDebut, dateFin));
    }

    @GetMapping("/filtrer")
    @Operation(summary = "Filtrer dynamiquement les véhicules via Criteria API / Specifications")
    public ResponseEntity<List<VehiculeDTO>> filtrerVehicules(
            @RequestParam(required = false) String ville,
            @RequestParam(required = false) CategorieVehicule categorie,
            @RequestParam(required = false) StatutVehicule statut,
            @RequestParam(required = false) BigDecimal maxTarif
    ) {
        return ResponseEntity.ok(vehiculeService.filtrerVehicules(ville, categorie, statut, maxTarif));
    }

    @PatchMapping("/{vehiculeId}/statut")
    @Operation(summary = "Changer le statut d'un véhicule")
    @RequiresRole({RoleEmploye.AGENT, RoleEmploye.MANAGER})
    public ResponseEntity<VehiculeDTO> changerStatut(
            @PathVariable Long vehiculeId,
            @RequestParam StatutVehicule statut
    ) {
        return ResponseEntity.ok(vehiculeService.changerStatutVehicule(vehiculeId, statut));
    }
}
