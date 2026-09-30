package com.tpgl.tpgl.service.pattern;

import com.tpgl.tpgl.domain.Reservation;
import com.tpgl.tpgl.domain.StatutReservation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Sujet / Diffuseur d'événements du patron GoF Observer pour les réservations.
 * Permet d'abonner/désabonner des observateurs et de leur notifier tout changement de statut.
 */
@Component
@Slf4j
public class ReservationEventPublisher {

    private final List<ReservationEventObserver> observers = new CopyOnWriteArrayList<>();

    public ReservationEventPublisher() {
    }

    @Autowired
    public ReservationEventPublisher(List<ReservationEventObserver> initialObservers) {
        if (initialObservers != null) {
            this.observers.addAll(initialObservers);
        }
    }

    public void registerObserver(ReservationEventObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void addObserver(ReservationEventObserver observer) {
        registerObserver(observer);
    }

    public void unregisterObserver(ReservationEventObserver observer) {
        observers.remove(observer);
    }

    public void removeObserver(ReservationEventObserver observer) {
        unregisterObserver(observer);
    }

    public List<ReservationEventObserver> getObservers() {
        return Collections.unmodifiableList(observers);
    }

    /**
     * Notifie tous les observateurs enregistrés d'un changement de statut de réservation.
     */
    public void notifyStatusChanged(
            Reservation reservation,
            StatutReservation ancienStatut,
            StatutReservation nouveauStatut
    ) {
        log.debug("Notification de {} observateur(s) pour la réservation #{} ({} -> {})",
                observers.size(),
                reservation != null ? reservation.getId() : null,
                ancienStatut,
                nouveauStatut);
        for (ReservationEventObserver observer : observers) {
            observer.onReservationStatusChanged(reservation, ancienStatut, nouveauStatut);
        }
    }

    public void publishStatusChange(
            Reservation reservation,
            StatutReservation ancienStatut,
            StatutReservation nouveauStatut
    ) {
        notifyStatusChanged(reservation, ancienStatut, nouveauStatut);
    }

    public void notifyStatusChange(
            Reservation reservation,
            StatutReservation ancienStatut,
            StatutReservation nouveauStatut
    ) {
        notifyStatusChanged(reservation, ancienStatut, nouveauStatut);
    }
}
