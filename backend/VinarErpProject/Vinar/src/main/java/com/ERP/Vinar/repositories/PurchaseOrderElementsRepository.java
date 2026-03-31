package com.ERP.Vinar.repositories;

import com.ERP.Vinar.entities.PurchaseOrderElements;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderElementsRepository extends JpaRepository<PurchaseOrderElements, Long> {
    List<PurchaseOrderElements> findByPurchaseOrderId(Long poId);
}
