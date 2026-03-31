package com.ERP.Vinar.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class PurchaseOrdersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNo;
    private String party;
    private String date;
    private String status;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    // ✅ Location added (Butibori/Jabalpur etc.)
    @Column(nullable = false, length = 100)
    private String location;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PurchaseOrderElements> elements = new ArrayList<>();

    @OneToMany(mappedBy = "po", cascade = CascadeType.ALL)
    private List<GRNEntity> grns = new ArrayList<>();

    public PurchaseOrdersEntity() {}

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

    public String getParty() { return party; }
    public void setParty(String party) { this.party = party; }

    public Vendor getVendor() { return vendor; }
    public void setVendor(Vendor vendor) { this.vendor = vendor; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public List<PurchaseOrderElements> getElements() { return elements; }
    public void setElements(List<PurchaseOrderElements> elements) { this.elements = elements; }

    public List<GRNEntity> getGrns() { return grns; }
    public void setGrns(List<GRNEntity> grns) { this.grns = grns; }
}
