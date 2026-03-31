package com.ERP.Vinar.controllers;

import com.ERP.Vinar.dto.KpiSummaryDto;
import com.ERP.Vinar.dto.LocationKpiDto;
import com.ERP.Vinar.services.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "http://localhost:4200")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/kpis")
    public ResponseEntity<KpiSummaryDto> getKpis() {
        return ResponseEntity.ok(analyticsService.getKpis());
    }

    @GetMapping("/locations")
    public ResponseEntity<List<LocationKpiDto>> getLocationKpis() {
        return ResponseEntity.ok(analyticsService.getLocationKpis());
    }

    @GetMapping("/site/{site}/kpis")
    public ResponseEntity<KpiSummaryDto> getSiteKpis(@PathVariable String site) {
        return ResponseEntity.ok(analyticsService.getSiteKpis(site));
    }
}
