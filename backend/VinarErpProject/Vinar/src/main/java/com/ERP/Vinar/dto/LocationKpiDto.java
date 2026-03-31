package com.ERP.Vinar.dto;

import java.math.BigDecimal;

public class LocationKpiDto {
    private String location;
    private Long totalItems;
    private BigDecimal totalQuantity;
    private Long uniqueMaterials;

    // JPQL constructor mapping
    public LocationKpiDto(String location, Long totalItems, BigDecimal totalQuantity, Long uniqueMaterials) {
        this.location = location;
        this.totalItems = totalItems;
        this.totalQuantity = totalQuantity;
        this.uniqueMaterials = uniqueMaterials;
    }

    public LocationKpiDto() {}

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Long totalItems) {
        this.totalItems = totalItems;
    }

    public BigDecimal getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(BigDecimal totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public Long getUniqueMaterials() {
        return uniqueMaterials;
    }

    public void setUniqueMaterials(Long uniqueMaterials) {
        this.uniqueMaterials = uniqueMaterials;
    }
}
