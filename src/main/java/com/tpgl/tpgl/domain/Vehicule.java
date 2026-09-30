package com.tpgl.tpgl.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un véhicule de la flotte AutoLoc.
 *
 * Justification Cascade & Fetch (Séance 3) :
 * - @ManyToOne vers Agence en FetchType.LAZY.
 * - @ManyToMany vers Equipement avec table de jointure "vehicule_equipement" en FetchType.LAZY.
 * - @OneToMany vers Maintenance et Reservation en FetchType.LAZY.
 */
@Entity
@Table(name = "vehicules", indexes = {
        @Index(name = "idx_vehicule_immat", columnList = "immatriculation", unique = true),
        @Index(name = "idx_vehicule_statut_categorie", columnList = "statut, categorie")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"agence", "equipements", "maintenances", "reservations"})
public class Vehicule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String immatriculation;

    @Column(nullable = false)
    private String marque;

    @Column(nullable = false)
    private String modele;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategorieVehicule categorie;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal tarifJournalier;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatutVehicule statut = StatutVehicule.DISPONIBLE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agence_id")
    @JsonIgnore
    private Agence agence;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "vehicule_equipement",
            joinColumns = @JoinColumn(name = "vehicule_id"),
            inverseJoinColumns = @JoinColumn(name = "equipement_id")
    )
    @Builder.Default
    private List<Equipement> equipements = new ArrayList<>();

    @OneToMany(mappedBy = "vehicule", fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnore
    private List<Maintenance> maintenances = new ArrayList<>();

    @OneToMany(mappedBy = "vehicule", fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnore
    private List<Reservation> reservations = new ArrayList<>();

    public Long getIdVehicule() {
        return id;
    }

    public void setIdVehicule(Long idVehicule) {
        this.id = idVehicule;
    }

    public void addEquipement(Equipement equipement) {
        if (!this.equipements.contains(equipement)) {
            this.equipements.add(equipement);
        }
        if (!equipement.getVehicules().contains(this)) {
            equipement.getVehicules().add(this);
        }
    }

    public void removeEquipement(Equipement equipement) {
        this.equipements.remove(equipement);
        equipement.getVehicules().remove(this);
    }

    public void addMaintenance(Maintenance maintenance) {
        if (!this.maintenances.contains(maintenance)) {
            this.maintenances.add(maintenance);
        }
        maintenance.setVehicule(this);
    }
}
