package com.ERP.Vinar.dto;

import com.ERP.Vinar.entities.JobWorkChallan;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
public class JobWorkChallanDTO {
    
    private Long id;
    
    @NotBlank(message = "Challan number is required")
    private String challanNo;
    
    @NotNull(message = "Challan date is required")
    private LocalDate challanDate;
    
    @NotBlank(message = "Supplier is required")
    private String supplier;
    
    private String vehicleNo;
    
    @Size(min = 1, message = "At least one line item is required")
    @Valid
    private List<JobWorkLineDTO> lines = new ArrayList<>();
    
    private JobWorkChallan.JobWorkStatus status;
    
    public static JobWorkChallanDTO fromEntity(JobWorkChallan challan) {
        JobWorkChallanDTO dto = new JobWorkChallanDTO();
        dto.setId(challan.getId());
        dto.setChallanNo(challan.getChallanNo());
        dto.setChallanDate(challan.getChallanDate());
        dto.setSupplier(challan.getSupplier());
        dto.setVehicleNo(challan.getVehicleNo());
        dto.setStatus(challan.getStatus());
        
        // Convert line items
        if (challan.getLines() != null) {
            challan.getLines().forEach(line -> 
                dto.getLines().add(JobWorkLineDTO.fromEntity(line))
            );
        }
        
        return dto;
    }
    
    public JobWorkChallan toEntity() {
        JobWorkChallan challan = new JobWorkChallan();
        return updateEntity(challan);
    }
    
    public JobWorkChallan updateEntity(JobWorkChallan challan) {
        challan.setChallanNo(this.challanNo);
        challan.setChallanDate(this.challanDate);
        challan.setSupplier(this.supplier);
        challan.setVehicleNo(this.vehicleNo);
        
        // Set status - default to ISSUED if not provided
        if (this.status != null) {
            challan.setStatus(this.status);
        } else {
            challan.setStatus(JobWorkChallan.JobWorkStatus.ISSUED);
        }
        
        // Update or add line items
        if (challan.getLines() == null) {
            challan.setLines(new ArrayList<>());
        }
        
        // Clear existing lines if any
        challan.getLines().clear();
        
        // Add all lines from DTO
        if (this.lines != null) {
            this.lines.forEach(lineDto -> {
                challan.addLine(lineDto.toEntity());
            });
        }
        
        return challan;
    }
}
