package com.tpgl.tpgl.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entité représentant un employé d'une agence AutoLoc (AGENT ou MANAGER).
 *
 * Justification Cascade & Fetch (Séance 3) :
 * - @ManyToOne en FetchType.LAZY pour ne charger l'agence associée que si nécessaire.
 */
@Entity
@Table(name = "employes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"agence"})
public class Employe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleEmploye role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agence_id")
    @JsonIgnore
    private Agence agence;

    public Long getIdEmploye() {
        return id;
    }

    public void setIdEmploye(Long idEmploye) {
        this.id = idEmploye;
    }
}
