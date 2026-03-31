package com.ERP.Vinar.dto;

import com.ERP.Vinar.entities.GRNItemEntity;
import java.math.BigDecimal;

public class GRNItemDTO {
    private Long grnItemId;
    private BigDecimal receivedQuantity;
    private String remarks;

    // Linked PO element details
    private String grade;
    private String section;
    private BigDecimal quantity;
    private BigDecimal alreadyReceived;
    private String material; // ✅ Added material field

    public static GRNItemDTO fromEntity(GRNItemEntity item) {
        GRNItemDTO dto = new GRNItemDTO();
        dto.setGrnItemId(item.getGrnItemId());
        dto.setReceivedQuantity(item.getReceivedQuantity());
        dto.setRemarks(item.getRemarks());

        if (item.getPoElement() != null) {
            dto.setGrade(item.getPoElement().getGrade());
            dto.setSection(item.getPoElement().getSection());
            dto.setQuantity(item.getPoElement().getQuantity());
            dto.setAlreadyReceived(item.getPoElement().getReceivedQuantity());
            dto.setMaterial(item.getPoElement().getMaterial()); // ✅ map material
        }

        return dto;
    }

    // Getters & setters
    public Long getGrnItemId() { return grnItemId; }
    public void setGrnItemId(Long grnItemId) { this.grnItemId = grnItemId; }

    public BigDecimal getReceivedQuantity() { return receivedQuantity; }
    public void setReceivedQuantity(BigDecimal receivedQuantity) { this.receivedQuantity = receivedQuantity; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getAlreadyReceived() { return alreadyReceived; }
    public void setAlreadyReceived(BigDecimal alreadyReceived) { this.alreadyReceived = alreadyReceived; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }
}
