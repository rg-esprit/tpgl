package com.tpgl.tpgl.repository;

import com.tpgl.tpgl.domain.Equipement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IEquipementRepository extends JpaRepository<Equipement, Long> {

    Optional<Equipement> findByLibelleIgnoreCase(String libelle);

    boolean existsByLibelleIgnoreCase(String libelle);
}
