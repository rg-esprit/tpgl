package com.tpgl.tpgl.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un contrat de location issu d'une réservation validée.
 *
 * Justification Cascade & Fetch (Séance 3) :
 * - @OneToOne avec Reservation.
 * - @OneToMany vers Paiement avec cascade = CascadeType.ALL et orphanRemoval = true :
 *   la suppression d'un contrat entraîne la suppression en cascade de tous ses paiements associés.
 */
@Entity
@Table(name = "contrats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"reservation", "paiements"})
public class Contrat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Builder.Default
    private LocalDate dateSignature = LocalDate.now();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal montantTotal;

    @Builder.Default
    private boolean valide = true;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", unique = true, nullable = false)
    @JsonIgnore
    private Reservation reservation;

    @OneToMany(mappedBy = "contrat", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnore
    private List<Paiement> paiements = new ArrayList<>();

    public Long getIdContrat() {
        return id;
    }

    public void setIdContrat(Long idContrat) {
        this.id = idContrat;
    }

    public void addPaiement(Paiement paiement) {
        if (!this.paiements.contains(paiement)) {
            this.paiements.add(paiement);
        }
        paiement.setContrat(this);
    }

    public void removePaiement(Paiement paiement) {
        this.paiements.remove(paiement);
        if (paiement.getContrat() == this) {
            paiement.setContrat(null);
        }
    }
}
