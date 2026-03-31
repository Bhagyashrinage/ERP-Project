package com.ERP.Vinar.dto;

import com.ERP.Vinar.entities.JobWorkReceipt;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;

@Data
public class JobWorkReceiptDTO {
    
    private Long id;
    
    @NotNull(message = "Challan ID is required")
    private Long challanId;
    
    @NotNull(message = "Challan number is required")
    private String challanNo;
    
    @NotNull(message = "Receipt date is required")
    private LocalDate receiptDate;
    
    @NotNull(message = "Receiver name is required")
    private String receiverName;
    
    @NotNull(message = "Quantity received is required")
    @PositiveOrZero(message = "Quantity must be zero or positive")
    private Double qtyReceived;
    
    @NotNull(message = "Actual weight is required")
    @PositiveOrZero(message = "Weight must be zero or positive")
    private Double actualWeight;
    
    @NotNull(message = "KATA weight is required")
    @PositiveOrZero(message = "KATA weight must be zero or positive")
    private Double kataWeight;
    
    private Double difference;
    
    @NotNull(message = "Job charge is required")
    @PositiveOrZero(message = "Job charge must be zero or positive")
    private Double jobCharge;
    
    private String remarks;
    
    private JobWorkReceipt.ReceiptStatus status;
    
    public static JobWorkReceiptDTO fromEntity(JobWorkReceipt receipt) {
        JobWorkReceiptDTO dto = new JobWorkReceiptDTO();
        dto.setId(receipt.getId());
        dto.setChallanId(receipt.getChallan().getId());
        dto.setChallanNo(receipt.getChallanNo());
        dto.setReceiptDate(receipt.getReceiptDate());
        dto.setReceiverName(receipt.getReceiverName());
        dto.setQtyReceived(receipt.getQtyReceived());
        dto.setActualWeight(receipt.getActualWeight());
        dto.setKataWeight(receipt.getKataWeight());
        dto.setDifference(receipt.getDifference());
        dto.setJobCharge(receipt.getJobCharge());
        dto.setRemarks(receipt.getRemarks());
        dto.setStatus(receipt.getStatus());
        return dto;
    }
    
    public JobWorkReceipt toEntity() {
        JobWorkReceipt receipt = new JobWorkReceipt();
        return updateEntity(receipt);
    }
    
    public JobWorkReceipt updateEntity(JobWorkReceipt receipt) {
        receipt.setId(this.id);
        receipt.setReceiptDate(this.receiptDate);
        receipt.setReceiverName(this.receiverName);
        receipt.setQtyReceived(this.qtyReceived);
        receipt.setActualWeight(this.actualWeight);
        receipt.setKataWeight(this.kataWeight);
        receipt.setDifference(this.difference != null ? this.difference : 
            (this.actualWeight != null && this.kataWeight != null) ? this.actualWeight - this.kataWeight : 0.0);
        receipt.setJobCharge(this.jobCharge);
        receipt.setRemarks(this.remarks);
        receipt.setStatus(this.status != null ? this.status : JobWorkReceipt.ReceiptStatus.PENDING);
        return receipt;
    }
}
