package com.ERP.Vinar.entities;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class GRNItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long grnItemId;

    private BigDecimal receivedQuantity = BigDecimal.ZERO;
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grn_id")
    private GRNEntity grn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "po_element_id")
    private PurchaseOrderElements poElement;

    public GRNItemEntity() {}

    public Long getGrnItemId() { return grnItemId; }
    public void setGrnItemId(Long grnItemId) { this.grnItemId = grnItemId; }

    public BigDecimal getReceivedQuantity() { return receivedQuantity; }
    public void setReceivedQuantity(BigDecimal receivedQuantity) { this.receivedQuantity = receivedQuantity; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public GRNEntity getGrn() { return grn; }
    public void setGrn(GRNEntity grn) { this.grn = grn; }

    public PurchaseOrderElements getPoElement() { return poElement; }
    public void setPoElement(PurchaseOrderElements poElement) { this.poElement = poElement; }
}
