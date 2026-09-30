package com.tpgl.tpgl.repository;

import com.tpgl.tpgl.domain.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    Optional<Client> findByNumPermis(String numPermis);

    List<Client> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(String nom, String prenom);

    Page<Client> findAll(Pageable pageable);

    @Query("SELECT DISTINCT c FROM Client c LEFT JOIN FETCH c.reservations WHERE c.id = :id")
    Optional<Client> findByIdWithReservations(@Param("id") Long id);
}
