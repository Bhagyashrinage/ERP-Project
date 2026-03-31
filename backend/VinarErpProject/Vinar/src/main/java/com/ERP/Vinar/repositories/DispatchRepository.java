package com.ERP.Vinar.repositories;

import com.ERP.Vinar.entities.DispatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DispatchRepository extends JpaRepository<DispatchEntity, Long> {
    Optional<DispatchEntity> findTopByChallanNoStartingWithOrderByChallanNoDesc(String prefix);
}
