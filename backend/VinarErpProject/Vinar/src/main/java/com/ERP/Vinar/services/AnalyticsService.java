package com.ERP.Vinar.services;

import com.ERP.Vinar.dto.KpiSummaryDto;
import com.ERP.Vinar.dto.LocationKpiDto;
import com.ERP.Vinar.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired private LoiRepository loiRepo;
    @Autowired private PurchaseOrderRepository poRepo;
    @Autowired private GRNRepository grnRepo;
    @Autowired private RMStockRepository stockRepo;
    @Autowired private RollingPlanRepository planRepo;
    @Autowired private PackingRepository packingRepo;
    @Autowired private DispatchRepository dispatchRepo;
    @Autowired private JobWorkRepository jobWorkRepo;

    public KpiSummaryDto getKpis() {
        KpiSummaryDto dto = new KpiSummaryDto();

        // LOIs
        List<?> lois = loiRepo.findAll();
        dto.setTotalLOIs(lois.size());
        long pending = lois.stream().filter(l -> {
            try { return ("Pending").equalsIgnoreCase((String)l.getClass().getMethod("getStatus").invoke(l)); }
            catch(Exception e){ return false; }
        }).count();
        long completed = lois.stream().filter(l -> {
            try { return ("Completed").equalsIgnoreCase((String)l.getClass().getMethod("getStatus").invoke(l)); }
            catch(Exception e){ return false; }
        }).count();
        dto.setPendingLOIs(pending);
        dto.setCompletedLOIs(completed);

        // POs and GRNs
        dto.setTotalPOs(poRepo.findAll().size());
        dto.setTotalGRNs(grnRepo.findAll().size());

        // Stock (sum)
        BigDecimal totalStock = stockRepo.sumQuantity();
        dto.setTotalStock(totalStock != null ? totalStock : BigDecimal.ZERO);

        // Production
        List<?> plans = planRepo.findAll();
        dto.setTotalProductions(plans.size());
        long completedPlans = plans.stream().filter(p -> {
            try { return ("Completed").equalsIgnoreCase((String)p.getClass().getMethod("getStatus").invoke(p)); }
            catch(Exception e){ return false; }
        }).count();
        dto.setCompletedProductions(completedPlans);

        // Packing
        dto.setTotalPacking(packingRepo.findAll().size());

        // Dispatch
        dto.setTotalDispatches(dispatchRepo.findAll().size());
        long dispatched = dispatchRepo.findAll().stream().filter(d -> {
            try { return ("Delivered").equalsIgnoreCase((String)d.getClass().getMethod("getStatus").invoke(d)); }
            catch(Exception e){ return false; }
        }).count();
        dto.setDispatchedItems(dispatched);

        // Jobwork
        dto.setTotalJobworkChallans(jobWorkRepo.findAll().size());
        long jobworkPending = jobWorkRepo.findAll().stream().filter(j -> {
            try { return ("Sent").equalsIgnoreCase((String)j.getClass().getMethod("getStatus").invoke(j)) || ("issued").equalsIgnoreCase((String)j.getClass().getMethod("getStatus").invoke(j)); }
            catch(Exception e){ return false; }
        }).count();
        dto.setJobworkPending(jobworkPending);

        return dto;
    }

    public List<LocationKpiDto> getLocationKpis() {
        // Use repository aggregation for efficiency
        return stockRepo.findLocationAggregation();
    }

    public KpiSummaryDto getSiteKpis(String site) {
        // Compute site-specific KPIs by filtering in-memory (safe and non-destructive)
        KpiSummaryDto dto = new KpiSummaryDto();

        // LOIs for site
        long totalLois = loiRepo.findAll().stream()
                .filter(l -> {
                    try { Object loc = l.getClass().getMethod("getLocation").invoke(l); return site.equalsIgnoreCase(loc == null ? "" : loc.toString()); }
                    catch(Exception e){ return false; }
                }).count();
        dto.setTotalLOIs(totalLois);

        // Stock for site
        List<LocationKpiDto> locs = getLocationKpis().stream().filter(l -> l.getLocation() != null && l.getLocation().equalsIgnoreCase(site)).collect(Collectors.toList());
        if(!locs.isEmpty()) dto.setTotalStock(locs.get(0).getTotalQuantity());

        return dto;
    }
}
