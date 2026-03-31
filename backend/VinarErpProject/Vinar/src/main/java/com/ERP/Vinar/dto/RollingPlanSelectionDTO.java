package com.ERP.Vinar.dto;

import java.util.List;

public class RollingPlanSelectionDTO {
    private Long loiId;
    private String mill;
    private String shift;
    private List<Selection> selectedStocks;

    public static class Selection {
        private Long loiItemId;
        private Long stockId;
        private Double plannedQty;

        public Long getLoiItemId() { return loiItemId; }
        public void setLoiItemId(Long loiItemId) { this.loiItemId = loiItemId; }

        public Long getStockId() { return stockId; }
        public void setStockId(Long stockId) { this.stockId = stockId; }

        public Double getPlannedQty() { return plannedQty; }
        public void setPlannedQty(Double plannedQty) { this.plannedQty = plannedQty; }
    }

    public Long getLoiId() { return loiId; }
    public void setLoiId(Long loiId) { this.loiId = loiId; }

    public String getMill() { return mill; }
    public void setMill(String mill) { this.mill = mill; }

    public String getShift() { return shift; }
    public void setShift(String shift) { this.shift = shift; }

    public List<Selection> getSelectedStocks() { return selectedStocks; }
    public void setSelectedStocks(List<Selection> selectedStocks) { this.selectedStocks = selectedStocks; }
}
