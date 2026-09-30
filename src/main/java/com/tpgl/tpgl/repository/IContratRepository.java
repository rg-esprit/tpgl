package com.tpgl.tpgl.repository;

import com.tpgl.tpgl.domain.Contrat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IContratRepository extends JpaRepository<Contrat, Long> {

    Optional<Contrat> findByReservationId(Long reservationId);

    List<Contrat> findByReservationClientId(Long clientId);

    List<Contrat> findByValideTrue();

    /**
     * Contrats actifs arrivant à échéance entre deux dates (pour alertes automatiques du Scheduler - Séance 11).
     */
    @Query("""
            SELECT c FROM Contrat c
            JOIN FETCH c.reservation r
            JOIN FETCH r.client
            JOIN FETCH r.vehicule
            WHERE c.valide = true
              AND r.dateFin BETWEEN :startDate AND :endDate
            """)
    List<Contrat> findContractsNearingExpiration(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    /**
     * Chiffre d'affaires total par agence (Séances 10 & 11 - Reporting & agrégations JPQL).
     */
    @Query("""
            SELECT COALESCE(SUM(c.montantTotal), 0)
            FROM Contrat c
            WHERE c.valide = true
              AND c.reservation.vehicule.agence.id = :agenceId
            """)
    BigDecimal sumChiffreAffairesByAgence(@Param("agenceId") Long agenceId);

    /**
     * Chiffre d'affaires global sur la plateforme.
     */
    @Query("SELECT COALESCE(SUM(c.montantTotal), 0) FROM Contrat c WHERE c.valide = true")
    BigDecimal sumChiffreAffairesGlobal();
}
