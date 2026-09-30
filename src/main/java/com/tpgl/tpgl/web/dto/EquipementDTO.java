package com.tpgl.tpgl.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipementDTO {

    private Long id;

    @NotBlank(message = "Le libellé de l'équipement est obligatoire")
    private String libelle;

    @Positive(message = "Le tarif journalier doit être strictement positif")
    private BigDecimal tarifJournalier;
}
