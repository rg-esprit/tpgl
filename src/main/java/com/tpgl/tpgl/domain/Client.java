package com.tpgl.tpgl.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un client AutoLoc.
 *
 * Justification Cascade & Fetch (Séance 3) :
 * - @OneToMany vers Reservation avec cascade = CascadeType.PERSIST et FetchType.LAZY :
 *   permet de persister un nouveau client avec ses réservations initiales sans supprimer
 *   l'historique en cascade sans contrôle.
 */
@Entity
@Table(name = "clients", indexes = {
        @Index(name = "idx_client_email", columnList = "email", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"reservations"})
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    private String telephone;

    @Column(nullable = false)
    private String numPermis;

    @Builder.Default
    private LocalDate dateInscription = LocalDate.now();

    @OneToMany(mappedBy = "client", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @Builder.Default
    @JsonIgnore
    private List<Reservation> reservations = new ArrayList<>();

    public Long getIdClient() {
        return id;
    }

    public void setIdClient(Long idClient) {
        this.id = idClient;
    }

    public void addReservation(Reservation reservation) {
        if (!this.reservations.contains(reservation)) {
            this.reservations.add(reservation);
        }
        reservation.setClient(this);
    }
}
