package com.tpgl.tpgl.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant une agence de location AutoLoc.
 *
 * Justification Cascade & Fetch (Séance 3) :
 * - @OneToMany vers Vehicule et Employe en FetchType.LAZY pour éviter le chargement
 *   systématique (problème N+1 / mémoire) lors de la consultation d'une agence.
 * - Aucune cascade de suppression (pas de CascadeType.REMOVE / ALL) sur Vehicule :
 *   la suppression d'une agence ne doit pas supprimer la flotte de véhicules.
 */
@Entity
@Table(name = "agences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"vehicules", "employes"})
public class Agence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String ville;

    private String adresse;

    private String telephone;

    @OneToMany(mappedBy = "agence", fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnore
    private List<Vehicule> vehicules = new ArrayList<>();

    @OneToMany(mappedBy = "agence", fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnore
    private List<Employe> employes = new ArrayList<>();

    public Long getIdAgence() {
        return id;
    }

    public void setIdAgence(Long idAgence) {
        this.id = idAgence;
    }

    public void addVehicule(Vehicule vehicule) {
        if (!this.vehicules.contains(vehicule)) {
            this.vehicules.add(vehicule);
        }
        vehicule.setAgence(this);
    }

    public void removeVehicule(Vehicule vehicule) {
        this.vehicules.remove(vehicule);
        if (vehicule.getAgence() == this) {
            vehicule.setAgence(null);
        }
    }

    public void addEmploye(Employe employe) {
        if (!this.employes.contains(employe)) {
            this.employes.add(employe);
        }
        employe.setAgence(this);
    }

    public void removeEmploye(Employe employe) {
        this.employes.remove(employe);
        if (employe.getAgence() == this) {
            employe.setAgence(null);
        }
    }
}
