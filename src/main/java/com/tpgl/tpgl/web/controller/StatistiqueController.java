package com.tpgl.tpgl.web.controller;

import com.tpgl.tpgl.aspect.RequiresRole;
import com.tpgl.tpgl.domain.RoleEmploye;
import com.tpgl.tpgl.scheduler.ContratEcheanceScheduler;
import com.tpgl.tpgl.service.IAgenceService;
import com.tpgl.tpgl.web.dto.StatistiqueAgenceDTO;
import com.tpgl.tpgl.web.dto.StatistiqueGlobaleDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Contrôleur REST pour le reporting, les statistiques et le déclenchement des tâches planifiées (Séances 10, 11 & 13).
 */
@RestController
@RequestMapping("/api/statistiques")
@RequiredArgsConstructor
@Tag(name = "Statistiques & Reporting", description = "Indicateurs d'occupation, chiffre d'affaires par agence et global")
public class StatistiqueController {

    private final IAgenceService agenceService;
    private final ContratEcheanceScheduler contratEcheanceScheduler;

    @GetMapping("/agences/{agenceId}")
    @Operation(summary = "Consulter les statistiques d'occupation et de chiffre d'affaires d'une agence")
    @RequiresRole({RoleEmploye.MANAGER})
    public ResponseEntity<StatistiqueAgenceDTO> getStatistiquesAgence(@PathVariable Long agenceId) {
        return ResponseEntity.ok(agenceService.getStatistiquesAgence(agenceId));
    }

    @GetMapping("/globales")
    @Operation(summary = "Consulter les statistiques globales de la plateforme AutoLoc")
    public ResponseEntity<StatistiqueGlobaleDTO> getStatistiquesGlobales() {
        return ResponseEntity.ok(agenceService.getStatistiquesGlobales());
    }

    @PostMapping("/scheduler/liberer-vehicules")
    @Operation(summary = "Exécuter manuellement la tâche planifiée de libération des véhicules en fin de location")
    public ResponseEntity<Map<String, Object>> declencherLiberationVehicules() {
        int nbLiberes = contratEcheanceScheduler.libererVehiculesEnFinDeLocation();
        return ResponseEntity.ok(Map.of(
                "tache", "libererVehiculesEnFinDeLocation",
                "vehiculesLiberes", nbLiberes
        ));
    }

    @PostMapping("/scheduler/alertes-echeance")
    @Operation(summary = "Exécuter manuellement la tâche planifiée d'alerte des contrats arrivant à échéance")
    public ResponseEntity<Map<String, Object>> declencherAlertesEcheance() {
        var contrats = contratEcheanceScheduler.alerterContratsArrivantAEcheance();
        return ResponseEntity.ok(Map.of(
                "tache", "alerterContratsArrivantAEcheance",
                "contratsEnAlerte", contrats.size()
        ));
    }
}
