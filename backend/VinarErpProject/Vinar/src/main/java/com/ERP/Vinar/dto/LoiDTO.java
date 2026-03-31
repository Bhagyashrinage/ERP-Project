package com.ERP.Vinar.dto;

import com.ERP.Vinar.entities.LoiEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class LoiDTO {

    private Long id;
    private String loiNumber;
    private String customerName;
    private String location;
    private String status;
    private LocalDate loiDate;
    private List<LoiItemDTO> items;

    public static LoiDTO fromEntity(LoiEntity loi) {
        LoiDTO dto = new LoiDTO();
        dto.setId(loi.getId());
        dto.setLoiNumber(loi.getLoiNumber());
        dto.setCustomerName(loi.getCustomerName());
        dto.setLocation(loi.getLocation());
        dto.setStatus(loi.getStatus());
        dto.setLoiDate(loi.getLoiDate());
        if (loi.getItems() != null) {
            dto.setItems(loi.getItems().stream()
                    .map(LoiItemDTO::fromEntity)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLoiNumber() { return loiNumber; }
    public void setLoiNumber(String loiNumber) { this.loiNumber = loiNumber; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getLoiDate() { return loiDate; }
    public void setLoiDate(LocalDate loiDate) { this.loiDate = loiDate; }

    public List<LoiItemDTO> getItems() { return items; }
    public void setItems(List<LoiItemDTO> items) { this.items = items; }
}
