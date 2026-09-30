package com.tpgl.tpgl.service;

import com.tpgl.tpgl.web.dto.ClientDTO;
import com.tpgl.tpgl.web.dto.ContratDTO;
import com.tpgl.tpgl.web.dto.ReservationResponseDTO;

import java.util.List;

/**
 * Interface du service métier de gestion des clients AutoLoc (Séance 8).
 */
public interface IClientService {

    ClientDTO inscrireClient(ClientDTO clientDTO);

    ClientDTO mettreAJourClient(Long id, ClientDTO clientDTO);

    ClientDTO getClientById(Long id);

    List<ClientDTO> getAllClients();

    void supprimerClient(Long id);

    List<ReservationResponseDTO> getHistoriqueReservations(Long clientId);

    List<ContratDTO> getHistoriqueContrats(Long clientId);
}
