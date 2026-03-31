package com.ERP.Vinar.dto;

import com.ERP.Vinar.entities.LoiItemEntity;
import java.math.BigDecimal;

public class LoiItemDTO {

    private Long id;
    private String material;
    private String grade;
    private String section;
    private BigDecimal length;
    private BigDecimal quantity;
    private String unit;

    public static LoiItemDTO fromEntity(LoiItemEntity e) {
        LoiItemDTO dto = new LoiItemDTO();
        dto.setId(e.getId());
        dto.setMaterial(e.getMaterial());
        dto.setGrade(e.getGrade());
        dto.setSection(e.getSection());
        dto.setLength(e.getLength());
        dto.setQuantity(e.getQuantity());
        dto.setUnit(e.getUnit());
        return dto;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public BigDecimal getLength() { return length; }
    public void setLength(BigDecimal length) { this.length = length; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
