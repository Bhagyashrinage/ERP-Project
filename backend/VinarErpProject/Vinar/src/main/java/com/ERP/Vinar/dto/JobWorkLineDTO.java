package com.ERP.Vinar.dto;

import com.ERP.Vinar.entities.JobWorkLine;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Data
public class JobWorkLineDTO {
    
    private Long id;
    
    @NotBlank(message = "Item code is required")
    private String itemCode;
    
    private String description;
    
    @NotNull(message = "Quantity is required")
    @PositiveOrZero(message = "Quantity must be zero or positive")
    private Double qtyIssued;
    
    @NotNull(message = "Weight is required")
    @PositiveOrZero(message = "Weight must be zero or positive")
    private Double weight;
    
    private String uom;
    
    private String remarks;
    
    public static JobWorkLineDTO fromEntity(JobWorkLine line) {
        JobWorkLineDTO dto = new JobWorkLineDTO();
        dto.setId(line.getId());
        dto.setItemCode(line.getItemCode());
        dto.setDescription(line.getDescription());
        dto.setQtyIssued(line.getQtyIssued());
        dto.setWeight(line.getWeight());
        dto.setUom(line.getUom());
        dto.setRemarks(line.getRemarks());
        return dto;
    }
    
    public JobWorkLine toEntity() {
        JobWorkLine line = new JobWorkLine();
        line.setId(this.id);
        line.setItemCode(this.itemCode);
        line.setDescription(this.description);
        line.setQtyIssued(this.qtyIssued);
        line.setWeight(this.weight);
        line.setUom(this.uom);
        line.setRemarks(this.remarks);
        return line;
    }
}
