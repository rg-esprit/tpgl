package com.tpgl.tpgl.service;

import com.tpgl.tpgl.domain.CategorieVehicule;
import com.tpgl.tpgl.domain.StatutVehicule;
import com.tpgl.tpgl.web.dto.VehiculeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Interface du service métier de gestion de la flotte de véhicules (Séance 8).
 */
public interface IVehiculeService {

    VehiculeDTO creerVehicule(VehiculeDTO vehiculeDTO);

    VehiculeDTO mettreAJourVehicule(Long id, VehiculeDTO vehiculeDTO);

    VehiculeDTO getVehiculeById(Long id);

    List<VehiculeDTO> getAllVehicules();

    Page<VehiculeDTO> getVehiculesParPage(Pageable pageable);

    void supprimerVehicule(Long id);

    VehiculeDTO affecterVehiculeAAgence(Long vehiculeId, Long agenceId);

    VehiculeDTO desaffecterVehiculeDAgence(Long vehiculeId);

    VehiculeDTO affecterEquipementAVehicule(Long vehiculeId, Long equipementId);

    VehiculeDTO desaffecterEquipementDeVehicule(Long vehiculeId, Long equipementId);

    List<VehiculeDTO> rechercherVehiculesDisponibles(
            String ville,
            CategorieVehicule categorie,
            LocalDate dateDebut,
            LocalDate dateFin
    );

    List<VehiculeDTO> filtrerVehicules(
            String ville,
            CategorieVehicule categorie,
            StatutVehicule statut,
            BigDecimal maxTarif
    );

    VehiculeDTO changerStatutVehicule(Long vehiculeId, StatutVehicule statut);
}
