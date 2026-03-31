package com.ERP.Vinar.dto;

public class RollingPlanItemDTO {
    private String material;
    private String grade;
    private String section;
    private Double length;
    private Double plannedQty;
    private Double completedQty;
    private String unit;
    private String remark;

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

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
