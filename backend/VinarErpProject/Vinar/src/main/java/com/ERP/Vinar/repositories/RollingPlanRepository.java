package com.ERP.Vinar.repositories;

import com.ERP.Vinar.entities.RollingPlanEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RollingPlanRepository extends JpaRepository<RollingPlanEntity, Long> {
    List<RollingPlanEntity> findByStatusIgnoreCaseAndPackingsIsEmpty(String status);
}
