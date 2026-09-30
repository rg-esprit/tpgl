package com.tpgl.tpgl.service;

import com.tpgl.tpgl.domain.Client;
import com.tpgl.tpgl.domain.Reservation;
import com.tpgl.tpgl.domain.StatutReservation;
import com.tpgl.tpgl.exception.BusinessException;
import com.tpgl.tpgl.exception.ResourceNotFoundException;
import com.tpgl.tpgl.repository.IClientRepository;
import com.tpgl.tpgl.repository.IContratRepository;
import com.tpgl.tpgl.repository.IPaiementRepository;
import com.tpgl.tpgl.repository.IReservationRepository;
import com.tpgl.tpgl.web.dto.AutoLocMapper;
import com.tpgl.tpgl.web.dto.ClientDTO;
import com.tpgl.tpgl.web.dto.ContratDTO;
import com.tpgl.tpgl.web.dto.ReservationResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du service métier de gestion des clients (Séance 8).
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ClientServiceImpl implements IClientService {

    private final IClientRepository clientRepository;
    private final IReservationRepository reservationRepository;
    private final IContratRepository contratRepository;
    private final IPaiementRepository paiementRepository;
    private final ITarificationService tarificationService;
    private final AutoLocMapper mapper;

    @Override
    public ClientDTO inscrireClient(ClientDTO clientDTO) {
        if (clientRepository.existsByEmailIgnoreCase(clientDTO.getEmail())) {
            throw new BusinessException("Un client avec l'adresse email '" + clientDTO.getEmail() + "' est déjà inscrit.");
        }

        Client client = mapper.toClientEntity(clientDTO);
        if (client.getDateInscription() == null) {
            client.setDateInscription(LocalDate.now());
        }

        Client saved = clientRepository.save(client);
        log.info("Nouveau client inscrit : {} {} (#{})", saved.getPrenom(), saved.getNom(), saved.getId());
        return mapper.toClientDto(saved);
    }

    @Override
    public ClientDTO mettreAJourClient(Long id, ClientDTO clientDTO) {
        Client client = getOrThrow(id);

        if (clientDTO.getEmail() != null
                && !clientDTO.getEmail().equalsIgnoreCase(client.getEmail())
                && clientRepository.existsByEmailIgnoreCase(clientDTO.getEmail())) {
            throw new BusinessException("L'adresse email '" + clientDTO.getEmail() + "' est déjà utilisée par un autre client.");
        }

        if (clientDTO.getNom() != null) {
            client.setNom(clientDTO.getNom());
        }
        if (clientDTO.getPrenom() != null) {
            client.setPrenom(clientDTO.getPrenom());
        }
        if (clientDTO.getEmail() != null) {
            client.setEmail(clientDTO.getEmail());
        }
        if (clientDTO.getTelephone() != null) {
            client.setTelephone(clientDTO.getTelephone());
        }
        if (clientDTO.getNumPermis() != null) {
            client.setNumPermis(clientDTO.getNumPermis());
        }

        return mapper.toClientDto(clientRepository.save(client));
    }

    @Override
    @Transactional(readOnly = true)
    public ClientDTO getClientById(Long id) {
        return mapper.toClientDto(getOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientDTO> getAllClients() {
        return clientRepository.findAll().stream()
                .map(mapper::toClientDto)
                .collect(Collectors.toList());
    }

    @Override
    public void supprimerClient(Long id) {
        Client client = getOrThrow(id);
        List<Reservation> reservations = reservationRepository.findByClientId(id);
        boolean hasActiveReservation = reservations.stream()
                .anyMatch(r -> r.getStatut() == StatutReservation.EN_ATTENTE || r.getStatut() == StatutReservation.CONFIRMEE);
        if (hasActiveReservation) {
            throw new BusinessException("Impossible de supprimer un client ayant une réservation active.");
        }
        clientRepository.delete(client);
        log.info("Client #{} supprimé.", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponseDTO> getHistoriqueReservations(Long clientId) {
        getOrThrow(clientId);
        return reservationRepository.findByClientId(clientId).stream()
                .map(r -> {
                    BigDecimal montant = r.getContrat() != null
                            ? r.getContrat().getMontantTotal()
                            : tarificationService.calculerTarifReservation(r.getVehicule(), r.getDateDebut(), r.getDateFin());
                    return mapper.toReservationResponseDto(r, montant);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContratDTO> getHistoriqueContrats(Long clientId) {
        getOrThrow(clientId);
        return contratRepository.findByReservationClientId(clientId).stream()
                .map(c -> mapper.toContratDto(c, paiementRepository.sumMontantByContratId(c.getId())))
                .collect(Collectors.toList());
    }

    private Client getOrThrow(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client", "id", id));
    }
}
