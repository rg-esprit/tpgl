package com.tpgl.tpgl.repository;

import com.tpgl.tpgl.domain.CategorieVehicule;
import com.tpgl.tpgl.domain.StatutReservation;
import com.tpgl.tpgl.domain.StatutVehicule;
import com.tpgl.tpgl.domain.Vehicule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface IVehiculeRepository extends JpaRepository<Vehicule, Long>, JpaSpecificationExecutor<Vehicule> {

    Optional<Vehicule> findByImmatriculation(String immatriculation);

    boolean existsByImmatriculation(String immatriculation);

    List<Vehicule> findByStatut(StatutVehicule statut);

    List<Vehicule> findByCategorie(CategorieVehicule categorie);

    List<Vehicule> findByCategorieAndStatut(CategorieVehicule categorie, StatutVehicule statut);

    List<Vehicule> findByAgenceId(Long agenceId);

    Page<Vehicule> findByStatut(StatutVehicule statut, Pageable pageable);

    List<Vehicule> findByTarifJournalierBetween(BigDecimal min, BigDecimal max);

    /**
     * Recherche de véhicules disponibles par ville, catégorie et période (Séances 10 & 11).
     */
    @Query("""
            SELECT v FROM Vehicule v
            JOIN v.agence a
            WHERE (:ville IS NULL OR LOWER(a.ville) = LOWER(:ville))
              AND (:categorie IS NULL OR v.categorie = :categorie)
              AND v.statut = com.tpgl.tpgl.domain.StatutVehicule.DISPONIBLE
              AND NOT EXISTS (
                  SELECT r FROM Reservation r
                  WHERE r.vehicule = v
                    AND r.statut IN :activeStatuses
                    AND r.dateDebut <= :dateFin
                    AND r.dateFin >= :dateDebut
              )
            """)
    List<Vehicule> findAvailableVehicles(
            @Param("ville") String ville,
            @Param("categorie") CategorieVehicule categorie,
            @Param("dateDebut") LocalDate dateDebut,
            @Param("dateFin") LocalDate dateFin,
            @Param("activeStatuses") List<StatutReservation> activeStatuses
    );

    @Query("SELECT COUNT(v) FROM Vehicule v WHERE v.agence.id = :agenceId")
    long countByAgenceId(@Param("agenceId") Long agenceId);

    @Query("SELECT COUNT(v) FROM Vehicule v WHERE v.agence.id = :agenceId AND v.statut = :statut")
    long countByAgenceIdAndStatut(@Param("agenceId") Long agenceId, @Param("statut") StatutVehicule statut);
}
