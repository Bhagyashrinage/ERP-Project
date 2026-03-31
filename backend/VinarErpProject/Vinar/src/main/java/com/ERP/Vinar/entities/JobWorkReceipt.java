package com.ERP.Vinar.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "job_work_receipts")
@Getter
@Setter
public class JobWorkReceipt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challan_id", nullable = false)
    @JsonBackReference(value = "challan-receipt")
    private JobWorkChallan challan;
    
    @Column(name = "challan_no", nullable = false)
    private String challanNo;
    
    @Column(name = "receipt_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate receiptDate;
    
    @Column(name = "receiver_name", nullable = false)
    private String receiverName;
    
    @Column(name = "qty_received", nullable = false)
    private double qtyReceived;
    
    @Column(name = "actual_weight", nullable = false)
    private double actualWeight;
    
    @Column(name = "kata_weight", nullable = false)
    private double kataWeight;
    
    @Column(nullable = false)
    private Double difference;
    
    public void setDifference(Double difference) {
        this.difference = difference;
    }
    
    @Column(name = "job_charge", nullable = false)
    private double jobCharge;
    
    private String remarks;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReceiptStatus status = ReceiptStatus.PENDING;
    
    @Column(name = "approved_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDate approvedAt;
    
    @Column(name = "approved_by")
    private String approvedBy;
    
    public enum ReceiptStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
