package com.ERP.Vinar.repositories;

import com.ERP.Vinar.entities.PurchaseOrdersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrdersEntity, Long> {
    Optional<PurchaseOrdersEntity> findTopByOrderNoStartingWithOrderByOrderNoDesc(String prefix);
}
