package com.tpgl.tpgl.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatistiqueAgenceDTO {

    private Long agenceId;

    private String agenceNom;

    private String ville;

    private long totalVehicules;

    private long vehiculesDisponibles;

    private long vehiculesLoues;

    private long vehiculesEnMaintenance;

    private double tauxOccupationPourcent;

    private long totalReservations;

    private BigDecimal chiffreAffairesTotal;
}
