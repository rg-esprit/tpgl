package com.tpgl.tpgl.web.controller;

import com.tpgl.tpgl.aspect.RequiresRole;
import com.tpgl.tpgl.domain.RoleEmploye;
import com.tpgl.tpgl.service.IContratService;
import com.tpgl.tpgl.web.dto.ContratDTO;
import com.tpgl.tpgl.web.dto.PaiementDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la contractualisation et les paiements (Séances 7, 8 & 9).
 */
@RestController
@RequestMapping("/api/contrats")
@RequiredArgsConstructor
@Tag(name = "Contrats & Paiements", description = "Établissement des contrats et enregistrement des paiements")
public class ContratController {

    private final IContratService contratService;

    @PostMapping("/reservation/{reservationId}")
    @Operation(summary = "Établir un contrat pour une réservation")
    @RequiresRole({RoleEmploye.AGENT, RoleEmploye.MANAGER})
    public ResponseEntity<ContratDTO> etablirContrat(@PathVariable Long reservationId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contratService.etablirContrat(reservationId));
    }

    @PostMapping("/{contratId}/paiements")
    @Operation(summary = "Enregistrer un paiement sur un contrat")
    @RequiresRole({RoleEmploye.AGENT, RoleEmploye.MANAGER})
    public ResponseEntity<PaiementDTO> enregistrerPaiement(
            @PathVariable Long contratId,
            @Valid @RequestBody PaiementDTO paiementDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(contratService.enregistrerPaiement(contratId, paiementDTO));
    }

    @GetMapping("/{contratId}")
    @Operation(summary = "Consulter un contrat par son identifiant")
    public ResponseEntity<ContratDTO> getContratById(@PathVariable Long contratId) {
        return ResponseEntity.ok(contratService.getContratById(contratId));
    }

    @GetMapping
    @Operation(summary = "Lister tous les contrats")
    public ResponseEntity<List<ContratDTO>> getAllContrats() {
        return ResponseEntity.ok(contratService.getAllContrats());
    }

    @GetMapping("/{contratId}/paiements")
    @Operation(summary = "Lister tous les paiements rattachés à un contrat")
    public ResponseEntity<List<PaiementDTO>> getPaiementsByContrat(@PathVariable Long contratId) {
        return ResponseEntity.ok(contratService.getPaiementsByContrat(contratId));
    }

    @DeleteMapping("/{contratId}")
    @Operation(summary = "Supprimer un contrat (supprime ses paiements en cascade = ALL)")
    @RequiresRole({RoleEmploye.MANAGER})
    public ResponseEntity<Void> supprimerContrat(@PathVariable Long contratId) {
        contratService.supprimerContrat(contratId);
        return ResponseEntity.noContent().build();
    }
}
