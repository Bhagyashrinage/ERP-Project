package com.ERP.Vinar.dto;

import com.ERP.Vinar.entities.PurchaseOrderElements;

import java.math.BigDecimal;

public class PurchaseOrderElementDTO {
    private Long id;
    private String material;
    private String grade;
    private String section;
    private BigDecimal steelWidth;
    private BigDecimal length;
    private Integer pcs;
    private BigDecimal quantity;
    private BigDecimal receivedQuantity;
    private String type;

    public static PurchaseOrderElementDTO fromEntity(PurchaseOrderElements element) {
        PurchaseOrderElementDTO dto = new PurchaseOrderElementDTO();
        dto.setId(element.getId());
        dto.setMaterial(element.getMaterial());
        dto.setGrade(element.getGrade());
        dto.setSection(element.getSection());
        dto.setSteelWidth(element.getSteelWidth());
        dto.setLength(element.getLength());
        dto.setPcs(element.getPcs());
        dto.setQuantity(element.getQuantity());
        dto.setReceivedQuantity(element.getReceivedQuantity());
        dto.setType(element.getType());
        return dto;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }
    public BigDecimal getSteelWidth() { return steelWidth; }
    public void setSteelWidth(BigDecimal steelWidth) { this.steelWidth = steelWidth; }
    public BigDecimal getLength() { return length; }
    public void setLength(BigDecimal length) { this.length = length; }
    public Integer getPcs() { return pcs; }
    public void setPcs(Integer pcs) { this.pcs = pcs; }
    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
    public BigDecimal getReceivedQuantity() { return receivedQuantity; }
    public void setReceivedQuantity(BigDecimal receivedQuantity) { this.receivedQuantity = receivedQuantity; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
