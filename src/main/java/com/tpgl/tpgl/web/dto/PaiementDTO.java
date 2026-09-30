package com.tpgl.tpgl.web.dto;

import com.tpgl.tpgl.domain.ModePaiement;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaiementDTO {

    private Long id;

    @NotNull(message = "Le montant du paiement est obligatoire")
    @Positive(message = "Le montant du paiement doit être strictement positif")
    private BigDecimal montant;

    private LocalDate datePaiement;

    @NotNull(message = "Le mode de paiement est obligatoire")
    private ModePaiement modePaiement;

    private Long contratId;
}
