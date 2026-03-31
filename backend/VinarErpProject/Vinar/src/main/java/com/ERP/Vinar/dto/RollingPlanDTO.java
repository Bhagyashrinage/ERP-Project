package com.ERP.Vinar.dto;

import java.time.LocalDate;
import java.util.List;

public class RollingPlanDTO {
    private Long id;
    private String planNumber;
    private LocalDate planDate;
    private String location;
    private String mill;
    private String shift;
    private String status;
    private Long loiId;
    private String loiNumber;
    private String customerName;
    private List<RollingPlanItemDTO> items;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlanNumber() { return planNumber; }
    public void setPlanNumber(String planNumber) { this.planNumber = planNumber; }

    public LocalDate getPlanDate() { return planDate; }
    public void setPlanDate(LocalDate planDate) { this.planDate = planDate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getMill() { return mill; }
    public void setMill(String mill) { this.mill = mill; }

    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getLoiId() { return loiId; }
    public void setLoiId(Long loiId) { this.loiId = loiId; }

    public String getLoiNumber() { return loiNumber; }
    public void setLoiNumber(String loiNumber) { this.loiNumber = loiNumber; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public List<RollingPlanItemDTO> getItems() { return items; }
    public void setItems(List<RollingPlanItemDTO> items) { this.items = items; }
}
