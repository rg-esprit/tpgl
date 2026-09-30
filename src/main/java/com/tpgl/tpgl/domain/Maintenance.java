package com.tpgl.tpgl.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entité représentant une opération de maintenance sur un véhicule.
 *
 * Justification Cascade & Fetch (Séance 3) :
 * - @ManyToOne avec CascadeType.PERSIST et FetchType.LAZY conformément au tableau du modèle conceptuel.
 */
@Entity
@Table(name = "maintenances")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"vehicule"})
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dateDebut;

    private LocalDate dateFin;

    @Column(nullable = false)
    private String description;

    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicule_id")
    @JsonIgnore
    private Vehicule vehicule;

    public Long getIdMaintenance() {
        return id;
    }

    public void setIdMaintenance(Long idMaintenance) {
        this.id = idMaintenance;
    }
}
