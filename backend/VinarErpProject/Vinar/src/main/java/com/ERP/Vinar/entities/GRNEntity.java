package com.ERP.Vinar.entities;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class GRNEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long grnId;

    private String grnNumber;
    private String vehicleNo;
    private LocalDate grnDate;

    // ✅ keep location here (auditable)
    @Column(nullable = false, length = 100)
    private String location;
    private String receiverName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "po_id")
    private PurchaseOrdersEntity po;

    @OneToMany(mappedBy = "grn", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GRNItemEntity> items = new ArrayList<>();

    public GRNEntity() {}

    public Long getGrnId() { return grnId; }
    public void setGrnId(Long grnId) { this.grnId = grnId; }

    public String getGrnNumber() { return grnNumber; }
    public void setGrnNumber(String grnNumber) { this.grnNumber = grnNumber; }

    public String getVehicleNo() { return vehicleNo; }
    public void setVehicleNo(String vehicleNo) { this.vehicleNo = vehicleNo; }

    public LocalDate getGrnDate() { return grnDate; }
    public void setGrnDate(LocalDate grnDate) { this.grnDate = grnDate; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public PurchaseOrdersEntity getPo() { return po; }
    public void setPo(PurchaseOrdersEntity po) { this.po = po; }

    public List<GRNItemEntity> getItems() { return items; }
    public void setItems(List<GRNItemEntity> items) { this.items = items; }
}
