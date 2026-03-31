package com.ERP.Vinar.repositories;

import com.ERP.Vinar.entities.RMStockEntity;
import com.ERP.Vinar.dto.LocationKpiDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface RMStockRepository extends JpaRepository<RMStockEntity, Long> {

    Optional<RMStockEntity> findByLocationAndMaterialAndGradeAndSectionAndSteelWidthAndLengthAndType(
            String location, String material, String grade, String section,
            BigDecimal steelWidth, BigDecimal length, String type
    );

    List<RMStockEntity> findByLocation(String location);

    List<RMStockEntity> findByMaterial(String material);

    List<RMStockEntity> findByLocationAndMaterial(String location, String material);
    
    @Query("SELECT SUM(r.totalQuantity) FROM RMStockEntity r")
    BigDecimal sumQuantity();
    
    @Query("SELECT new com.ERP.Vinar.dto.LocationKpiDto(r.location, COUNT(r), SUM(r.totalQuantity), COUNT(DISTINCT r.material)) " +
           "FROM RMStockEntity r GROUP BY r.location")
    List<LocationKpiDto> findLocationAggregation();
}
