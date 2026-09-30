package com.tpgl.tpgl.web.dto;

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
public class ContratDTO {

    private Long id;

    private LocalDate dateSignature;

    private BigDecimal montantTotal;

    private boolean valide;

    private Long reservationId;

    private String clientNomComplet;

    private String vehiculeImmatriculation;

    private BigDecimal montantPaye;

    private BigDecimal resteAPayer;
}
