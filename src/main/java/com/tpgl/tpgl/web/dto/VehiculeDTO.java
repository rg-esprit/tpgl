package com.tpgl.tpgl.web.dto;

import com.tpgl.tpgl.domain.CategorieVehicule;
import com.tpgl.tpgl.domain.StatutVehicule;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehiculeDTO {

    private Long id;

    @NotBlank(message = "L'immatriculation est obligatoire")
    private String immatriculation;

    @NotBlank(message = "La marque est obligatoire")
    private String marque;

    @NotBlank(message = "Le modèle est obligatoire")
    private String modele;

    @NotNull(message = "La catégorie du véhicule est obligatoire")
    private CategorieVehicule categorie;

    @NotNull(message = "Le tarif journalier est obligatoire")
    @Positive(message = "Le tarif journalier doit être strictement positif")
    private BigDecimal tarifJournalier;

    @Builder.Default
    private StatutVehicule statut = StatutVehicule.DISPONIBLE;

    private Long agenceId;

    private String agenceNom;

    @Builder.Default
    private List<Long> equipementIds = new ArrayList<>();

    @Builder.Default
    private List<EquipementDTO> equipements = new ArrayList<>();
}
