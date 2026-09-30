package com.tpgl.tpgl.web.controller;

import com.tpgl.tpgl.aspect.RequiresRole;
import com.tpgl.tpgl.domain.RoleEmploye;
import com.tpgl.tpgl.service.IReservationService;
import com.tpgl.tpgl.web.dto.ReservationRequestDTO;
import com.tpgl.tpgl.web.dto.ReservationResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des réservations AutoLoc (Séances 7, 8 & 9).
 */
@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Tag(name = "Réservations", description = "Création, validation, annulation et suivi des réservations")
public class ReservationController {

    private final IReservationService reservationService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle réservation")
    public ResponseEntity<ReservationResponseDTO> creerReservation(
            @Valid @RequestBody ReservationRequestDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationService.creerReservation(request));
    }

    @PutMapping("/{id}/valider")
    @Operation(summary = "Valider une réservation et générer le contrat associé")
    @RequiresRole({RoleEmploye.AGENT, RoleEmploye.MANAGER})
    public ResponseEntity<ReservationResponseDTO> validerReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.validerReservation(id));
    }

    @PutMapping("/{id}/annuler")
    @Operation(summary = "Annuler une réservation")
    public ResponseEntity<ReservationResponseDTO> annulerReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.annulerReservation(id));
    }

    @PutMapping("/{id}/terminer")
    @Operation(summary = "Terminer une réservation et libérer le véhicule")
    @RequiresRole({RoleEmploye.AGENT, RoleEmploye.MANAGER})
    public ResponseEntity<ReservationResponseDTO> terminerReservation(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.terminerReservation(id));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter une réservation par son identifiant")
    public ResponseEntity<ReservationResponseDTO> getReservationById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getReservationById(id));
    }

    @GetMapping
    @Operation(summary = "Lister toutes les réservations")
    public ResponseEntity<List<ReservationResponseDTO>> getAllReservations() {
        return ResponseEntity.ok(reservationService.getAllReservations());
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Lister les réservations d'un client")
    public ResponseEntity<List<ReservationResponseDTO>> getReservationsByClient(@PathVariable Long clientId) {
        return ResponseEntity.ok(reservationService.getReservationsByClient(clientId));
    }
}
