package com.ERP.Vinar.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import javax.persistence.*;

@Entity
@Table(name = "rolling_plan_item")
public class RollingPlanItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String material;
    private String grade;
    private String section;
    private Double length;
    private Double plannedQty;
    private Double completedQty = 0.0;
    private Double pendingQty = 0.0;
    private String unit;
    private String remark;

    @ManyToOne
    @JoinColumn(name = "rolling_plan_id")
    @JsonBackReference(value = "rollingPlanItemsRef")
    private RollingPlanEntity rollingPlan;

    public RollingPlanItemEntity() {}

    @PrePersist
    @PreUpdate
    private void calculatePendingQty() {
        if (plannedQty != null && completedQty != null) {
            this.pendingQty = plannedQty - completedQty;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMaterial() { return material; }
    public void setMaterial(String material) { this.material = material; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getSection() { return section; }
    public void setSection(String section) { this.section = section; }

    public Double getLength() { return length; }
    public void setLength(Double length) { this.length = length; }

    public Double getPlannedQty() { return plannedQty; }
    public void setPlannedQty(Double plannedQty) { this.plannedQty = plannedQty; }

    public Double getCompletedQty() { return completedQty; }
    public void setCompletedQty(Double completedQty) { this.completedQty = completedQty; }

    public Double getPendingQty() { return pendingQty; }
    public void setPendingQty(Double pendingQty) { this.pendingQty = pendingQty; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public RollingPlanEntity getRollingPlan() { return rollingPlan; }
    public void setRollingPlan(RollingPlanEntity rollingPlan) { this.rollingPlan = rollingPlan; }
}
