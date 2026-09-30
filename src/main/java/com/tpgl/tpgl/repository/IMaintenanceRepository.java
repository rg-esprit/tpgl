package com.tpgl.tpgl.repository;

import com.tpgl.tpgl.domain.Maintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IMaintenanceRepository extends JpaRepository<Maintenance, Long> {

    List<Maintenance> findByVehiculeId(Long vehiculeId);
}
