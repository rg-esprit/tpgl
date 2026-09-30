package com.tpgl.tpgl.service.pattern;

import com.tpgl.tpgl.domain.Reservation;
import com.tpgl.tpgl.domain.StatutReservation;

/**
 * Interface du patron de conception GoF Observer pour écouter les changements d'état des réservations.
 */
public interface ReservationEventObserver {

    /**
     * Méthode appelée lorsqu'une réservation change de statut.
     *
     * @param reservation   la réservation concernée
     * @param ancienStatut  le statut précédent de la réservation
     * @param nouveauStatut le nouveau statut appliqué
     */
    void onReservationStatusChanged(
            Reservation reservation,
            StatutReservation ancienStatut,
            StatutReservation nouveauStatut
    );
}
