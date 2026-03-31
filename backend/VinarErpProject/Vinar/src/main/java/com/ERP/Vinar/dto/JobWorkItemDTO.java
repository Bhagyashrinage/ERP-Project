package com.ERP.Vinar.dto;

import com.ERP.Vinar.entities.JobWorkItem;
import lombok.Data;

@Data
public class JobWorkItemDTO {
    private Long id;
    private String itemName;
    private double quantity;
    private String unit;

    public static JobWorkItemDTO fromEntity(JobWorkItem entity) {
        JobWorkItemDTO dto = new JobWorkItemDTO();
        dto.setId(entity.getId());
        dto.setItemName(entity.getItemName());
        dto.setQuantity(entity.getQuantity());
        dto.setUnit(entity.getUnit());
        return dto;
    }
}
