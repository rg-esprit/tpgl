package com.tpgl.tpgl.service.pattern;

import com.tpgl.tpgl.domain.Reservation;
import com.tpgl.tpgl.domain.StatutReservation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Observateur GoF chargé d'envoyer une notification au client lors d'un changement de statut de sa réservation.
 * Conserve un historique en mémoire des notifications émises pour vérification et tests.
 */
@Component
@Slf4j
public class NotificationObserver implements ReservationEventObserver {

    private final List<String> notifications = new CopyOnWriteArrayList<>();

    @Override
    public void onReservationStatusChanged(
            Reservation reservation,
            StatutReservation ancienStatut,
            StatutReservation nouveauStatut
    ) {
        Long reservationId = reservation != null ? reservation.getId() : null;
        String emailClient = (reservation != null && reservation.getClient() != null)
                ? reservation.getClient().getEmail()
                : "client-inconnu";

        String message = String.format(
                "[NOTIFICATION] Réservation #%s (client: %s) : transition de statut %s -> %s",
                reservationId, emailClient, ancienStatut, nouveauStatut
        );
        notifications.add(message);
        log.info(message);
    }

    public List<String> getNotifications() {
        return Collections.unmodifiableList(notifications);
    }

    public List<String> getEvents() {
        return getNotifications();
    }

    public void clear() {
        notifications.clear();
    }
}
