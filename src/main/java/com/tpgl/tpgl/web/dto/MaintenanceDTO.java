package com.tpgl.tpgl.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceDTO {

    private Long id;

    @NotNull(message = "La date de début de maintenance est obligatoire")
    private LocalDate dateDebut;

    private LocalDate dateFin;

    @NotBlank(message = "La description de la maintenance est obligatoire")
    private String description;

    @NotNull(message = "L'identifiant du véhicule est obligatoire")
    private Long vehiculeId;

    private String vehiculeImmatriculation;
}
