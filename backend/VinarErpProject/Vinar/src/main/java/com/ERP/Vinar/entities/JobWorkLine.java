package com.ERP.Vinar.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "job_work_lines")
@Getter
@Setter
public class JobWorkLine {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "item_code", nullable = false)
    private String itemCode;
    
    @Column(nullable = false)
    private String description;
    
    @Column(name = "qty_issued", nullable = false)
    private double qtyIssued;
    
    @Column(nullable = false)
    private double weight;
    
    @Column(nullable = false)
    private String uom;
    
    private String remarks;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challan_id", nullable = false)
    @JsonBackReference(value = "challan-lines")
    private JobWorkChallan challan;
}
