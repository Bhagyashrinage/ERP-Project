package com.ERP.Vinar.entities;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "job_work")
public class JobWorkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobworkId;
    private String vendorId;
    private String vendorName;
    private String vendorContact;
    private String poId;
    
    private String operation; // Type of work (Cutting, Machining, Coating, etc.)
    private String materialDescription;
    
    private Double materialSentQty;
    private Double materialReceivedQty;
    
    private LocalDate dateSent;
    private LocalDate dateReceived;
    private LocalDate expectedReturnDate;
    
    private String status; // Sent, In Progress, Received, Completed
    
    private Double billingAmount;
    private String billingStatus; // Pending, Paid
    
    private String remarks;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobworkId() {
        return jobworkId;
    }

    public void setJobworkId(String jobworkId) {
        this.jobworkId = jobworkId;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorContact() {
        return vendorContact;
    }

    public void setVendorContact(String vendorContact) {
        this.vendorContact = vendorContact;
    }

    public String getPoId() {
        return poId;
    }

    public void setPoId(String poId) {
        this.poId = poId;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getMaterialDescription() {
        return materialDescription;
    }

    public void setMaterialDescription(String materialDescription) {
        this.materialDescription = materialDescription;
    }

    public Double getMaterialSentQty() {
        return materialSentQty;
    }

    public void setMaterialSentQty(Double materialSentQty) {
        this.materialSentQty = materialSentQty;
    }

    public Double getMaterialReceivedQty() {
        return materialReceivedQty;
    }

    public void setMaterialReceivedQty(Double materialReceivedQty) {
        this.materialReceivedQty = materialReceivedQty;
    }

    public LocalDate getDateSent() {
        return dateSent;
    }

    public void setDateSent(LocalDate dateSent) {
        this.dateSent = dateSent;
    }

    public LocalDate getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(LocalDate dateReceived) {
        this.dateReceived = dateReceived;
    }

    public LocalDate getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public void setExpectedReturnDate(LocalDate expectedReturnDate) {
        this.expectedReturnDate = expectedReturnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Double getBillingAmount() {
        return billingAmount;
    }

    public void setBillingAmount(Double billingAmount) {
        this.billingAmount = billingAmount;
    }

    public String getBillingStatus() {
        return billingStatus;
    }

    public void setBillingStatus(String billingStatus) {
        this.billingStatus = billingStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
