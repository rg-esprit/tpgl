package com.tpgl.tpgl.web.dto;

import com.tpgl.tpgl.domain.StatutReservation;
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
public class ReservationResponseDTO {

    private Long id;

    private LocalDate dateDebut;

    private LocalDate dateFin;

    private StatutReservation statut;

    private Long clientId;

    private String clientNomComplet;

    private Long vehiculeId;

    private String vehiculeImmatriculation;

    private String vehiculeMarqueModele;

    private BigDecimal montantEstime;

    private Long contratId;
}
