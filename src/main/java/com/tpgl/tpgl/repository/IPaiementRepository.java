package com.tpgl.tpgl.repository;

import com.tpgl.tpgl.domain.ModePaiement;
import com.tpgl.tpgl.domain.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface IPaiementRepository extends JpaRepository<Paiement, Long> {

    List<Paiement> findByContratId(Long contratId);

    List<Paiement> findByModePaiement(ModePaiement modePaiement);

    @Query("SELECT COALESCE(SUM(p.montant), 0) FROM Paiement p WHERE p.contrat.id = :contratId")
    BigDecimal sumMontantByContratId(@Param("contratId") Long contratId);
}
