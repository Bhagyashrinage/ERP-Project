package com.ERP.Vinar.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "dispatch")
public class DispatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dispatchId;
    private String invoiceNo;
    private LocalDate invoiceDate;
    private String section;
    private String customer;
    private Double qtyIssued;
    private String vehicleNo;
    private String jobworkNo;
    private String challanNo;
    private String poNo;
    private LocalDate poOrderDate;
    private String fromLocation;
    private String destination;
    private String status;
    private LocalDate dispatchDate;

    // Relationship with PackingEntity (one packing can have one dispatch)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "packing_id")
    @JsonBackReference
    private PackingEntity packing;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDispatchId() {
        return dispatchId;
    }

    public void setDispatchId(String dispatchId) {
        this.dispatchId = dispatchId;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public LocalDate getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDate invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public Double getQtyIssued() {
        return qtyIssued;
    }

    public void setQtyIssued(Double qtyIssued) {
        this.qtyIssued = qtyIssued;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getJobworkNo() {
        return jobworkNo;
    }

    public void setJobworkNo(String jobworkNo) {
        this.jobworkNo = jobworkNo;
    }

    public String getChallanNo() {
        return challanNo;
    }

    public void setChallanNo(String challanNo) {
        this.challanNo = challanNo;
    }

    public String getPoNo() {
        return poNo;
    }

    public void setPoNo(String poNo) {
        this.poNo = poNo;
    }

    public LocalDate getPoOrderDate() {
        return poOrderDate;
    }

    public void setPoOrderDate(LocalDate poOrderDate) {
        this.poOrderDate = poOrderDate;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDispatchDate() {
        return dispatchDate;
    }

    public void setDispatchDate(LocalDate dispatchDate) {
        this.dispatchDate = dispatchDate;
    }

    public PackingEntity getPacking() {
        return packing;
    }

    public void setPacking(PackingEntity packing) {
        this.packing = packing;
    }
}
