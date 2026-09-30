package com.tpgl.tpgl.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entité représentant un paiement rattaché à un contrat de location.
 *
 * Justification Cascade & Fetch (Séance 3) :
 * - @ManyToOne vers Contrat en FetchType.LAZY.
 */
@Entity
@Table(name = "paiements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"contrat"})
public class Paiement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    @Column(nullable = false)
    @Builder.Default
    private LocalDate datePaiement = LocalDate.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ModePaiement modePaiement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrat_id", nullable = false)
    @JsonIgnore
    private Contrat contrat;

    public Long getIdPaiement() {
        return id;
    }

    public void setIdPaiement(Long idPaiement) {
        this.id = idPaiement;
    }
}
