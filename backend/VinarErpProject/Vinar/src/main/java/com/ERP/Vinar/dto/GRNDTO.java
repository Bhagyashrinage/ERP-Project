package com.ERP.Vinar.dto;

import com.ERP.Vinar.entities.GRNEntity;
import java.util.List;
import java.util.stream.Collectors;

public class GRNDTO {
    private Long grnId;
    private String grnNumber;
    private String vehicleNo;
    private String grnDate;
    private String orderNo;
    private String location;
    private List<GRNItemDTO> items;

    public static GRNDTO fromEntity(GRNEntity grn) {
        GRNDTO dto = new GRNDTO();
        dto.setGrnId(grn.getGrnId());
        dto.setGrnNumber(grn.getGrnNumber());
        dto.setVehicleNo(grn.getVehicleNo());
        dto.setGrnDate(grn.getGrnDate() != null ? grn.getGrnDate().toString() : null);
        dto.setLocation(grn.getLocation());
        dto.setOrderNo(grn.getPo() != null ? grn.getPo().getOrderNo() : null);

        if (grn.getItems() != null) {
            dto.setItems(grn.getItems().stream()
                    .map(GRNItemDTO::fromEntity)
                    .collect(Collectors.toList()));
        }

        return dto;
    }

    public Long getGrnId() { return grnId; }
    public void setGrnId(Long grnId) { this.grnId = grnId; }

    public String getGrnNumber() { return grnNumber; }
    public void setGrnNumber(String grnNumber) { this.grnNumber = grnNumber; }

    public String getVehicleNo() { return vehicleNo; }
    public void setVehicleNo(String vehicleNo) { this.vehicleNo = vehicleNo; }

    public String getGrnDate() { return grnDate; }
    public void setGrnDate(String grnDate) { this.grnDate = grnDate; }

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public List<GRNItemDTO> getItems() { return items; }
    public void setItems(List<GRNItemDTO> items) { this.items = items; }
}
