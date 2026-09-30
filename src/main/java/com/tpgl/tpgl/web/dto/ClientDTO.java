package com.tpgl.tpgl.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientDTO {

    private Long id;

    @NotBlank(message = "Le nom du client est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom du client est obligatoire")
    private String prenom;

    @NotBlank(message = "L'adresse email est obligatoire")
    @Email(message = "Le format de l'adresse email est invalide")
    private String email;

    private String telephone;

    @NotBlank(message = "Le numéro de permis est obligatoire")
    private String numPermis;

    private LocalDate dateInscription;
}
