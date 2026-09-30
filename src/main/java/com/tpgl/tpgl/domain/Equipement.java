package com.tpgl.tpgl.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un équipement optionnel (GPS, siège bébé, coffre de toit...).
 *
 * Justification Cascade & Fetch (Séance 3) :
 * - @ManyToMany(mappedBy = "equipements", fetch = FetchType.LAZY) pour éviter de charger
 *   tous les véhicules possédant cet équipement à chaque lecture.
 */
@Entity
@Table(name = "equipements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"vehicules"})
public class Equipement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String libelle;

    @Builder.Default
    @Column(precision = 10, scale = 2)
    private BigDecimal tarifJournalier = new BigDecimal("5.00");

    @ManyToMany(mappedBy = "equipements", fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnore
    private List<Vehicule> vehicules = new ArrayList<>();

    public Long getIdEquipement() {
        return id;
    }

    public void setIdEquipement(Long idEquipement) {
        this.id = idEquipement;
    }
}
