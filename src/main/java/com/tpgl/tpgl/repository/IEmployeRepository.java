package com.tpgl.tpgl.repository;

import com.tpgl.tpgl.domain.Employe;
import com.tpgl.tpgl.domain.RoleEmploye;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEmployeRepository extends JpaRepository<Employe, Long> {

    List<Employe> findByAgenceId(Long agenceId);

    List<Employe> findByRole(RoleEmploye role);

    List<Employe> findByAgenceIdAndRole(Long agenceId, RoleEmploye role);
}
