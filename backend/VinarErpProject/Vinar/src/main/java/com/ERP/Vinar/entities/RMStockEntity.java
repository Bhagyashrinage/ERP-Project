package com.ERP.Vinar.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "rm_stock")
public class RMStockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String material;       // Billet / Angle / Channel...
    private String grade;          // MS / HT / SS...
    private String section;        // e.g., S1 or 80x80x6
    private BigDecimal length;     // length in mm
    private BigDecimal steelWidth; // ✅ NEW: width in mm
    private String type;           // KG / PCS / LENGTH

    private BigDecimal totalQuantity = BigDecimal.ZERO;

    @Column(nullable = false, length = 100)
    private String location;       // Butibori / Jabalpur

    private LocalDateTime lastUpdated = LocalDateTime.now();

    public RMStockEntity() {}

    // ✅ Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public BigDecimal getLength() { return length; }
    public void setLength(BigDecimal length) { this.length = length; }

    public BigDecimal getSteelWidth() { return steelWidth; }
    public void setSteelWidth(BigDecimal steelWidth) { this.steelWidth = steelWidth; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public BigDecimal getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(BigDecimal totalQuantity) { this.totalQuantity = totalQuantity; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
