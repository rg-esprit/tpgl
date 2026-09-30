package com.tpgl.tpgl.repository;

import com.tpgl.tpgl.domain.Agence;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IAgenceRepository extends JpaRepository<Agence, Long> {

    List<Agence> findByVilleIgnoreCase(String ville);

    Optional<Agence> findByNomIgnoreCase(String nom);

    Page<Agence> findByVilleContainingIgnoreCase(String ville, Pageable pageable);

    @Query("SELECT DISTINCT a FROM Agence a LEFT JOIN FETCH a.vehicules WHERE a.id = :id")
    Optional<Agence> findByIdWithVehicules(@Param("id") Long id);
}
