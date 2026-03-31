package com.ERP.Vinar.entities;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "loi_entity")
public class LoiEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loi_number", length = 50, unique = true)
    private String loiNumber;              // e.g., LOI-2025-3001

    @Column(name = "loi_date")
    private LocalDate loiDate;

    @Column(name = "customer_name", length = 255)
    private String customerName;

    @Column(nullable = false, length = 100)
    private String location;               // Butibori / Jabalpur

    @Column(length = 50)
    private String status;                 // Pending / Planned / Completed

    @OneToMany(mappedBy = "loi", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<LoiItemEntity> items = new ArrayList<>();

    public LoiEntity() {}

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getLoiNumber() { return loiNumber; }
    public void setLoiNumber(String loiNumber) { this.loiNumber = loiNumber; }

    public LocalDate getLoiDate() { return loiDate; }
    public void setLoiDate(LocalDate loiDate) { this.loiDate = loiDate; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<LoiItemEntity> getItems() { return items; }
    public void setItems(List<LoiItemEntity> items) { this.items = items; }
}
