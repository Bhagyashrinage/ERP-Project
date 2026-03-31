package com.ERP.Vinar.dto;

import java.math.BigDecimal;

public class GRNItemUpdateRequest {

    private Long poElementId;          // ✅ Added: to link GRN item with correct PO element
    private BigDecimal receivedQty;
    private String remarks;

    public Long getPoElementId() {
        return poElementId;
    }

    public void setPoElementId(Long poElementId) {
        this.poElementId = poElementId;
    }

    public BigDecimal getReceivedQty() {
        return receivedQty;
    }

    public void setReceivedQty(BigDecimal receivedQty) {
        this.receivedQty = receivedQty;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
