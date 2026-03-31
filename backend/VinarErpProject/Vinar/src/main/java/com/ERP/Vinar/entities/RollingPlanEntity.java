package com.ERP.Vinar.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "rolling_plan")
public class RollingPlanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String planNumber;
    private LocalDate planDate;
    private String location;
    private String mill;
    private String shift;
    private String status; // Planned / In Progress / Completed

    @ManyToOne
    @JoinColumn(name = "loi_id")
    private LoiEntity loi;

    // ✅ This represents the items (production plan details)
    @OneToMany(mappedBy = "rollingPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "rollingPlanItemsRef")
    private List<RollingPlanItemEntity> items;

    // ✅ Also link to packings
    @OneToMany(mappedBy = "rollingPlan", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "rollingPlanPackingsRef")
    private List<PackingEntity> packings;

    // ---------- Constructors ----------
    public RollingPlanEntity() {}

    // ---------- Getters & Setters ----------
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

    public LoiEntity getLoi() { return loi; }
    public void setLoi(LoiEntity loi) { this.loi = loi; }

    // ✅ Old methods (used by controller)
    public List<RollingPlanItemEntity> getItems() {
        return items;
    }

    public void setItems(List<RollingPlanItemEntity> items) {
        this.items = items;
        if (items != null) {
            for (RollingPlanItemEntity item : items) {
                item.setRollingPlan(this);
            }
        }
    }

    // ✅ New methods (used for Packing linkage)
    public List<PackingEntity> getPackings() {
        return packings;
    }

    public void setPackings(List<PackingEntity> packings) {
        this.packings = packings;
        // PackingEntity does not define setRollingPlan(RollingPlanEntity) — 
        // manage the inverse association on the PackingEntity side if needed.
    }
}
