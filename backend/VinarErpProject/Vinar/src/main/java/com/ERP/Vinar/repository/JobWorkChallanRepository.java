package com.ERP.Vinar.repository;

import com.ERP.Vinar.entities.JobWorkChallan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobWorkChallanRepository extends JpaRepository<JobWorkChallan, Long> {
    
    boolean existsByChallanNo(String challanNo);
    
    Optional<JobWorkChallan> findByChallanNo(String challanNo);
    
    @Query("SELECT c FROM JobWorkChallan c WHERE " +
           "(:challanNo IS NULL OR c.challanNo LIKE %:challanNo%) AND " +
           "(:supplier IS NULL OR c.supplier LIKE %:supplier%) AND " +
           "(:status IS NULL OR c.status = :status)")
    List<JobWorkChallan> search(
        @Param("challanNo") String challanNo,
        @Param("supplier") String supplier,
        @Param("status") JobWorkChallan.JobWorkStatus status
    );
    
    @Query("SELECT c FROM JobWorkChallan c LEFT JOIN FETCH c.lines WHERE c.id = :id")
    Optional<JobWorkChallan> findByIdWithLines(@Param("id") Long id);
}
