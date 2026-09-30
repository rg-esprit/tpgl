package com.tpgl.tpgl.repository;

import com.tpgl.tpgl.domain.Reservation;
import com.tpgl.tpgl.domain.StatutReservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface IReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByClientId(Long clientId);

    List<Reservation> findByVehiculeId(Long vehiculeId);

    List<Reservation> findByStatut(StatutReservation statut);

    Page<Reservation> findByStatut(StatutReservation statut, Pageable pageable);

    @Query("""
            SELECT COUNT(r) > 0 FROM Reservation r
            WHERE r.vehicule.id = :vehiculeId
              AND r.statut IN :activeStatuses
              AND r.dateDebut <= :dateFin
              AND r.dateFin >= :dateDebut
            """)
    boolean existsOverlappingReservation(
            @Param("vehiculeId") Long vehiculeId,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin,
            @Param("activeStatuses") List<StatutReservation> activeStatuses
    );

    /**
     * Réservations confirmées dont la date de fin est dépassée (pour libération automatique par Scheduler).
     */
    @Query("SELECT r FROM Reservation r JOIN FETCH r.vehicule WHERE r.statut = :statut AND r.dateFin < :date")
    List<Reservation> findExpiredReservations(@Param("statut") StatutReservation statut, @Param("date") LocalDate date);

    @Query("SELECT COUNT(r) FROM Reservation r WHERE r.vehicule.agence.id = :agenceId")
    long countByAgenceId(@Param("agenceId") Long agenceId);
}
