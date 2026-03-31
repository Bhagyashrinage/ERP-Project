package com.ERP.Vinar.repository;

import com.ERP.Vinar.entities.JobWorkReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobWorkReceiptRepository extends JpaRepository<JobWorkReceipt, Long> {
    
    @Query("SELECT r FROM JobWorkReceipt r JOIN r.challan c WHERE " +
           "c.challanNo LIKE %:challanNo% AND " +
           "(:status IS NULL OR r.status = :status)")
    List<JobWorkReceipt> search(
        @Param("challanNo") String challanNo,
        @Param("status") JobWorkReceipt.ReceiptStatus status
    );
    
    boolean existsByChallanId(Long challanId);
    
    @Query("SELECT r FROM JobWorkReceipt r LEFT JOIN FETCH r.challan c LEFT JOIN FETCH c.lines WHERE r.id = :id")
    Optional<JobWorkReceipt> findByIdWithChallanAndLines(@Param("id") Long id);
}
