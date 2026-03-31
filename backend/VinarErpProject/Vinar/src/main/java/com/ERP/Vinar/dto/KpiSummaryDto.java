package com.ERP.Vinar.dto;

import java.math.BigDecimal;

public class KpiSummaryDto {
    private long totalLOIs;
    private long pendingLOIs;
    private long completedLOIs;
    private long totalPOs;
    private long totalGRNs;
    private BigDecimal totalStock;
    private long totalProductions;
    private long completedProductions;
    private long totalPacking;
    private long totalDispatches;
    private long dispatchedItems;
    private long totalJobworkChallans;
    private long jobworkPending;

    public KpiSummaryDto() {}

    public long getTotalLOIs() { return totalLOIs; }
    public void setTotalLOIs(long totalLOIs) { this.totalLOIs = totalLOIs; }

    public long getPendingLOIs() { return pendingLOIs; }
    public void setPendingLOIs(long pendingLOIs) { this.pendingLOIs = pendingLOIs; }

    public long getCompletedLOIs() { return completedLOIs; }
    public void setCompletedLOIs(long completedLOIs) { this.completedLOIs = completedLOIs; }

    public long getTotalPOs() { return totalPOs; }
    public void setTotalPOs(long totalPOs) { this.totalPOs = totalPOs; }

    public long getTotalGRNs() { return totalGRNs; }
    public void setTotalGRNs(long totalGRNs) { this.totalGRNs = totalGRNs; }

    public BigDecimal getTotalStock() { return totalStock; }
    public void setTotalStock(BigDecimal totalStock) { this.totalStock = totalStock; }

    public long getTotalProductions() { return totalProductions; }
    public void setTotalProductions(long totalProductions) { this.totalProductions = totalProductions; }

    public long getCompletedProductions() { return completedProductions; }
    public void setCompletedProductions(long completedProductions) { this.completedProductions = completedProductions; }

    public long getTotalPacking() { return totalPacking; }
    public void setTotalPacking(long totalPacking) { this.totalPacking = totalPacking; }

    public long getTotalDispatches() { return totalDispatches; }
    public void setTotalDispatches(long totalDispatches) { this.totalDispatches = totalDispatches; }

    public long getDispatchedItems() { return dispatchedItems; }
    public void setDispatchedItems(long dispatchedItems) { this.dispatchedItems = dispatchedItems; }

    public long getTotalJobworkChallans() { return totalJobworkChallans; }
    public void setTotalJobworkChallans(long totalJobworkChallans) { this.totalJobworkChallans = totalJobworkChallans; }

    public long getJobworkPending() { return jobworkPending; }
    public void setJobworkPending(long jobworkPending) { this.jobworkPending = jobworkPending; }
}
