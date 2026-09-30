package com.tpgl.tpgl.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entité représentant une réservation de véhicule par un client.
 *
 * Justification Cascade & Fetch (Séance 3) :
 * - Deux @ManyToOne (Client, Vehicule) en FetchType.LAZY.
 * - @OneToOne bidirectionnelle avec Contrat.
 */
@Entity
@Table(name = "reservations", indexes = {
        @Index(name = "idx_reservation_dates", columnList = "dateDebut, dateFin"),
        @Index(name = "idx_reservation_statut", columnList = "statut")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"client", "vehicule", "contrat"})
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dateDebut;

    @Column(nullable = false)
    private LocalDate dateFin;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatutReservation statut = StatutReservation.EN_ATTENTE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnore
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicule_id", nullable = false)
    @JsonIgnore
    private Vehicule vehicule;

    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Contrat contrat;

    public Long getIdReservation() {
        return id;
    }

    public void setIdReservation(Long idReservation) {
        this.id = idReservation;
    }
}
