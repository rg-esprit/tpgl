package com.tpgl.tpgl.web.dto;

import com.tpgl.tpgl.domain.RoleEmploye;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeDTO {

    private Long id;

    @NotBlank(message = "Le nom de l'employé est obligatoire")
    private String nom;

    @NotBlank(message = "Le prénom de l'employé est obligatoire")
    private String prenom;

    @NotNull(message = "Le rôle de l'employé est obligatoire")
    private RoleEmploye role;

    private Long agenceId;

    private String agenceNom;
}
