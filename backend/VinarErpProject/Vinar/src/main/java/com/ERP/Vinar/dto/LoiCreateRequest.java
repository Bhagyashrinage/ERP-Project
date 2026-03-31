package com.ERP.Vinar.dto;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

public class LoiCreateRequest {

    private String loiNumber;         // optional; if null, backend autogenerates
    private LocalDate loiDate;
    private String customerName;
    private String location;
    private List<Item> items;         // required
    // status auto = "Pending" on create

    public static class Item {
        public String material;
        public String grade;
        public String section;
        public BigDecimal length;
        public BigDecimal quantity;
        public String unit;
    }

    public String getLoiNumber() { return loiNumber; }
    public void setLoiNumber(String loiNumber) { this.loiNumber = loiNumber; }
    public LocalDate getLoiDate() { return loiDate; }
    public void setLoiDate(LocalDate loiDate) { this.loiDate = loiDate; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }
}
