package com.ERP.Vinar.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "job_work_challans")
@Getter
@Setter
public class JobWorkChallan {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "challan_no", nullable = false, unique = true)
    private String challanNo;
    
    @Column(name = "challan_date", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate challanDate;
    
    @Column(nullable = false)
    private String supplier;
    
    @Column(name = "vehicle_no")
    private String vehicleNo;
    
    @OneToMany(mappedBy = "challan", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "challan-lines")
    private List<JobWorkLine> lines = new ArrayList<>();
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private JobWorkStatus status = JobWorkStatus.ISSUED;
    
    @Column(name = "created_at", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDate createdAt = LocalDate.now();
    
    @OneToMany(mappedBy = "challan", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference(value = "challan-receipt")
    private List<JobWorkReceipt> receipts = new ArrayList<>();
    
    // Helper method to add line items
    public void addLine(JobWorkLine line) {
        lines.add(line);
        line.setChallan(this);
    }
    
    // Helper method to remove line items
    public void removeLine(JobWorkLine line) {
        lines.remove(line);
        line.setChallan(null);
    }
    
    public enum JobWorkStatus {
        ISSUED,
        PENDING_RETURN,
        RETURNED,
        COMPLETED
    }
}
