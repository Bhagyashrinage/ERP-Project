package com.ERP.Vinar.repositories;

import com.ERP.Vinar.entities.JobWorkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobWorkRepository extends JpaRepository<JobWorkEntity, Long> {
    List<JobWorkEntity> findByStatus(String status);
    List<JobWorkEntity> findByVendorId(String vendorId);
    List<JobWorkEntity> findByPoId(String poId);
}
