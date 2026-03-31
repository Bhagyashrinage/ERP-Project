package com.ERP.Vinar.repositories;

import com.ERP.Vinar.entities.GRNItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GRNItemRepository extends JpaRepository<GRNItemEntity, Long> {
    List<GRNItemEntity> findByGrnGrnId(Long grnId);
}
