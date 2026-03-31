package com.ERP.Vinar.repositories;

import com.ERP.Vinar.entities.PackingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PackingRepository extends JpaRepository<PackingEntity, Long> {
    List<PackingEntity> findByStatus(String status);
}
