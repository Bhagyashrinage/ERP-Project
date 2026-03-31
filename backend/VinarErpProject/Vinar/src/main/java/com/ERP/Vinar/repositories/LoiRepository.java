package com.ERP.Vinar.repositories;

import com.ERP.Vinar.entities.LoiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LoiRepository extends JpaRepository<LoiEntity, Long> {
    boolean existsByLoiNumber(String loiNumber);
    Optional<LoiEntity> findTopByLoiNumberStartingWithOrderByLoiNumberDesc(String prefix);
}
