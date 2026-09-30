package com.tpgl.tpgl.web.controller;

import com.tpgl.tpgl.service.IClientService;
import com.tpgl.tpgl.web.dto.ClientDTO;
import com.tpgl.tpgl.web.dto.ContratDTO;
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
 * Contrôleur REST pour la gestion des clients AutoLoc (Séances 7 & 9).
 */
@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
@Tag(name = "Clients", description = "Inscription des clients, profil et historique des réservations/contrats")
public class ClientController {

    private final IClientService clientService;

    @PostMapping
    @Operation(summary = "Inscrire un nouveau client")
    public ResponseEntity<ClientDTO> inscrireClient(@Valid @RequestBody ClientDTO clientDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.inscrireClient(clientDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour le profil d'un client")
    public ResponseEntity<ClientDTO> mettreAJourClient(
            @PathVariable Long id,
            @Valid @RequestBody ClientDTO clientDTO
    ) {
        return ResponseEntity.ok(clientService.mettreAJourClient(id, clientDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un client par son identifiant")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable Long id) {
        return ResponseEntity.ok(clientService.getClientById(id));
    }

    @GetMapping
    @Operation(summary = "Lister tous les clients")
    public ResponseEntity<List<ClientDTO>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer un client")
    public ResponseEntity<Void> supprimerClient(@PathVariable Long id) {
        clientService.supprimerClient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{clientId}/reservations")
    @Operation(summary = "Consulter l'historique des réservations d'un client")
    public ResponseEntity<List<ReservationResponseDTO>> getHistoriqueReservations(@PathVariable Long clientId) {
        return ResponseEntity.ok(clientService.getHistoriqueReservations(clientId));
    }

    @GetMapping("/{clientId}/contrats")
    @Operation(summary = "Consulter l'historique des contrats d'un client")
    public ResponseEntity<List<ContratDTO>> getHistoriqueContrats(@PathVariable Long clientId) {
        return ResponseEntity.ok(clientService.getHistoriqueContrats(clientId));
    }
}
