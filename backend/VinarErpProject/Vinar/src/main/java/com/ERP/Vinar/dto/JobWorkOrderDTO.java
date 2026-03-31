package com.ERP.Vinar.dto;

import com.ERP.Vinar.entities.JobWorkOrder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class JobWorkOrderDTO {
    private Long id;
    private String orderNumber;
    private String customerName;
    private LocalDate orderDate;
    private String status;
    private List<JobWorkItemDTO> items;

    public static JobWorkOrderDTO fromEntity(JobWorkOrder entity) {
        JobWorkOrderDTO dto = new JobWorkOrderDTO();
        dto.setId(entity.getId());
        dto.setOrderNumber(entity.getOrderNumber());
        dto.setCustomerName(entity.getCustomerName());
        dto.setOrderDate(entity.getOrderDate());
        dto.setStatus(entity.getStatus());
        dto.setItems(entity.getItems().stream().map(JobWorkItemDTO::fromEntity).collect(Collectors.toList()));
        return dto;
    }
}
