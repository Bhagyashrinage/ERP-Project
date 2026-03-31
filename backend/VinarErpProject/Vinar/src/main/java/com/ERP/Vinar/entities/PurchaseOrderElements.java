package com.ERP.Vinar.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
public class PurchaseOrderElements {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String material;     // Billet / Angle / Channel
    private String grade;        // HT / MS / SS
    private String section;      // S1/S2... or 80x80x6 (as per your data)
    private BigDecimal steelWidth;
    private BigDecimal length;
    private Integer pcs;
    private BigDecimal quantity;
    private BigDecimal receivedQuantity = BigDecimal.ZERO;
    private String type;         // KG / PCS / LENGTH

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_id")
    private PurchaseOrdersEntity purchaseOrder;

    @OneToMany(mappedBy = "poElement", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GRNItemEntity> grnItems = new ArrayList<>();

    public PurchaseOrderElements() {}

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public BigDecimal getSteelWidth() { return steelWidth; }
    public void setSteelWidth(BigDecimal steelWidth) { this.steelWidth = steelWidth; }

    public BigDecimal getLength() { return length; }
    public void setLength(BigDecimal length) { this.length = length; }

    public Integer getPcs() { return pcs; }
    public void setPcs(Integer pcs) { this.pcs = pcs; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getReceivedQuantity() { return receivedQuantity; }
    public void setReceivedQuantity(BigDecimal receivedQuantity) { this.receivedQuantity = receivedQuantity; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public PurchaseOrdersEntity getPurchaseOrder() { return purchaseOrder; }
    public void setPurchaseOrder(PurchaseOrdersEntity purchaseOrder) { this.purchaseOrder = purchaseOrder; }

    public List<GRNItemEntity> getGrnItems() { return grnItems; }
    public void setGrnItems(List<GRNItemEntity> grnItems) { this.grnItems = grnItems; }
}
