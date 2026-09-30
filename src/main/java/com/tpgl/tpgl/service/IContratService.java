package com.tpgl.tpgl.service;

import com.tpgl.tpgl.web.dto.ContratDTO;
import com.tpgl.tpgl.web.dto.PaiementDTO;

import java.util.List;

/**
 * Interface du service métier de gestion des contrats et paiements (Séances 8 & 9).
 */
public interface IContratService {

    ContratDTO etablirContrat(Long reservationId);

    PaiementDTO enregistrerPaiement(Long contratId, PaiementDTO paiementDTO);

    ContratDTO getContratById(Long contratId);

    List<ContratDTO> getAllContrats();

    List<PaiementDTO> getPaiementsByContrat(Long contratId);

    void supprimerContrat(Long contratId);
}
