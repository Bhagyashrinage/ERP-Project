package com.ERP.Vinar.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "packing")
@JsonIgnoreProperties(ignoreUnknown = true)
public class PackingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer srNo;
    private String poNo;
    private String grade;
    private String colourCode;
    private String gradeSection;
    private String sectionWt;
    private Double length;
    private Integer noOfPcs;
    private Double qtyInMt;
    private String heatNo;
    private Double challanQty;
    private String challanNo;

    // Header Info
    private String customer;
    private String docNo;

    // Jackson will parse ISO date string (e.g. "2025-11-13") to LocalDate
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate packingDate;

    private String lorryNo;

    // Status field (PACKED / DISPATCHED)
    private String status;

    // Relationship with RollingPlanEntity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rolling_plan_id")
    @JsonBackReference(value = "rollingPlanPackingsRef")
    private RollingPlanEntity rollingPlan;

    // Relationship with DispatchEntity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_id")
    @JsonBackReference
    private DispatchEntity dispatch;

    public PackingEntity() {}

    // Getters & Setters (your existing ones)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Integer getSrNo() { return srNo; }
    public void setSrNo(Integer srNo) { this.srNo = srNo; }
    public String getPoNo() { return poNo; }
    public void setPoNo(String poNo) { this.poNo = poNo; }
    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }
    public String getColourCode() { return colourCode; }
    public void setColourCode(String colourCode) { this.colourCode = colourCode; }
    public String getGradeSection() { return gradeSection; }
    public void setGradeSection(String gradeSection) { this.gradeSection = gradeSection; }
    public String getSectionWt() { return sectionWt; }
    public void setSectionWt(String sectionWt) { this.sectionWt = sectionWt; }
    public Double getLength() { return length; }
    public void setLength(Double length) { this.length = length; }
    public Integer getNoOfPcs() { return noOfPcs; }
    public void setNoOfPcs(Integer noOfPcs) { this.noOfPcs = noOfPcs; }
    public Double getQtyInMt() { return qtyInMt; }
    public void setQtyInMt(Double qtyInMt) { this.qtyInMt = qtyInMt; }
    public String getHeatNo() { return heatNo; }
    public void setHeatNo(String heatNo) { this.heatNo = heatNo; }
    public Double getChallanQty() { return challanQty; }
    public void setChallanQty(Double challanQty) { this.challanQty = challanQty; }
    public String getChallanNo() { return challanNo; }
    public void setChallanNo(String challanNo) { this.challanNo = challanNo; }
    public String getCustomer() { return customer; }
    public void setCustomer(String customer) { this.customer = customer; }
    public String getDocNo() { return docNo; }
    public void setDocNo(String docNo) { this.docNo = docNo; }
    public LocalDate getPackingDate() { return packingDate; }
    public void setPackingDate(LocalDate packingDate) { this.packingDate = packingDate; }
    public String getLorryNo() { return lorryNo; }
    public void setLorryNo(String lorryNo) { this.lorryNo = lorryNo; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public DispatchEntity getDispatch() { return dispatch; }
    public void setDispatch(DispatchEntity dispatch) { this.dispatch = dispatch; }
    public RollingPlanEntity getRollingPlan() { return rollingPlan; }
    public void setRollingPlan(RollingPlanEntity rollingPlan) { this.rollingPlan = rollingPlan; }
}
