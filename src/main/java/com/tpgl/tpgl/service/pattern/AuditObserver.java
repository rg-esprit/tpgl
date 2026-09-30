package com.tpgl.tpgl.service.pattern;

import com.tpgl.tpgl.domain.Reservation;
import com.tpgl.tpgl.domain.StatutReservation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Observateur GoF chargé de la journalisation d'audit lors des transitions d'état des réservations.
 * Conserve en mémoire la liste des entrées d'audit pour consultation et vérification.
 */
@Component
@Slf4j
public class AuditObserver implements ReservationEventObserver {

    private final List<String> auditLogs = new CopyOnWriteArrayList<>();

    @Override
    public void onReservationStatusChanged(
            Reservation reservation,
            StatutReservation ancienStatut,
            StatutReservation nouveauStatut
    ) {
        Long reservationId = reservation != null ? reservation.getId() : null;
        String entry = String.format(
                "[AUDIT][%s] Réservation #%s : %s -> %s",
                LocalDateTime.now(), reservationId, ancienStatut, nouveauStatut
        );
        auditLogs.add(entry);
        log.info(entry);
    }

    public List<String> getAuditLogs() {
        return Collections.unmodifiableList(auditLogs);
    }

    public List<String> getEvents() {
        return getAuditLogs();
    }

    public void clear() {
        auditLogs.clear();
    }
}
