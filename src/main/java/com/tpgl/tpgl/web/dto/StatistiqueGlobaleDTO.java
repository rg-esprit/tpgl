package com.tpgl.tpgl.web.dto;

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
public class StatistiqueGlobaleDTO {

    private long totalAgences;

    private long totalVehicules;

    private long totalClients;

    private long totalReservations;

    private long totalContratsValides;

    private double tauxOccupationGlobalPourcent;

    private BigDecimal chiffreAffairesGlobal;

    @Builder.Default
    private List<StatistiqueAgenceDTO> statistiquesParAgence = new ArrayList<>();
}
