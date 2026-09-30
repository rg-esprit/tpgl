package com.tpgl.tpgl.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgenceDTO {

    private Long id;

    @NotBlank(message = "Le nom de l'agence est obligatoire")
    private String nom;

    @NotBlank(message = "La ville de l'agence est obligatoire")
    private String ville;

    private String adresse;

    private String telephone;

    private int nombreVehicules;

    private int nombreEmployes;
}
