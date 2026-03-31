package com.ERP.Vinar.dto;

import java.time.LocalDate;
import java.util.List;

public class GRNCreateRequest {
    private String grnNumber;
    private String vehicleNo;
    private LocalDate grnDate;
    private String receiverName;
    private List<GRNItemUpdateRequest> items;

    // Getters & setters
    public String getGrnNumber() { return grnNumber; }
    public void setGrnNumber(String grnNumber) { this.grnNumber = grnNumber; }

    public String getVehicleNo() { return vehicleNo; }
    public void setVehicleNo(String vehicleNo) { this.vehicleNo = vehicleNo; }

    public LocalDate getGrnDate() { return grnDate; }
    public void setGrnDate(LocalDate grnDate) { this.grnDate = grnDate; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public List<GRNItemUpdateRequest> getItems() { return items; }
    public void setItems(List<GRNItemUpdateRequest> items) { this.items = items; }
}
