package com.ERP.Vinar.entities;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "loi_item_entity") //Loi Matlab Orders by Customers
public class LoiItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Finished Goods specs required by customer
    @Column(length = 255)
    private String material;          // Angle / Channel / Round etc.

    @Column(length = 100)
    private String grade;             // MS / HT etc.

    @Column(length = 100)
    private String section;           // e.g., 80x80x6 or S1

    @Column(precision = 19, scale = 2)
    private BigDecimal length;        // Finished length (mm)

    @Column(precision = 19, scale = 2)
    private BigDecimal quantity;      // Required qty

    @Column(length = 50)
    private String unit;              // KG / TON / PCS

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loi_id")
    private LoiEntity loi;

    public LoiItemEntity() {}

    // Getters & Setters
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

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public LoiEntity getLoi() { return loi; }
    public void setLoi(LoiEntity loi) { this.loi = loi; }
}
