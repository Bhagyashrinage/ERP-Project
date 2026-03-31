package com.ERP.Vinar.controllers;

import com.ERP.Vinar.entities.PackingEntity;
import com.ERP.Vinar.entities.DispatchEntity;
import com.ERP.Vinar.entities.RollingPlanEntity;
import com.ERP.Vinar.repositories.PackingRepository;
import com.ERP.Vinar.repositories.DispatchRepository;
import com.ERP.Vinar.repositories.RollingPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/packing")
@CrossOrigin(origins = "http://localhost:4200")
public class PackingController {

    @Autowired
    private PackingRepository packingRepository;

    @Autowired
    private DispatchRepository dispatchRepository;

    @Autowired
    private RollingPlanRepository rollingPlanRepository;

    // ✅ Get all packing entries
    @GetMapping
    public ResponseEntity<List<PackingEntity>> getAllPacking() {
        return ResponseEntity.ok(packingRepository.findAll());
    }

    // ✅ Get only packed items
    @GetMapping("/packed")
    public ResponseEntity<List<PackingEntity>> getPackedItems() {
        return ResponseEntity.ok(packingRepository.findByStatus("PACKED"));
    }

    // ✅ Get only dispatched items
    @GetMapping("/dispatched")
    public ResponseEntity<List<PackingEntity>> getDispatchedItems() {
        return ResponseEntity.ok(packingRepository.findByStatus("DISPATCHED"));
    }

    // ✅ Create new packing manually
    @PostMapping
    public ResponseEntity<PackingEntity> createPacking(@RequestBody PackingEntity packing) {
        packing.setStatus("PACKED");
        PackingEntity saved = packingRepository.save(packing);
        return ResponseEntity.ok(saved);
    }

    // ✅ Create packing from a completed production plan
    @PostMapping("/from-production/{planId}")
    @Transactional
    public ResponseEntity<Map<String, Object>> createPackingFromProduction(
            @PathVariable Long planId,
            @RequestBody PackingEntity packingData) {

        Map<String, Object> response = new HashMap<>();

        return rollingPlanRepository.findById(planId).map(plan -> {
            if (!"Completed".equalsIgnoreCase(plan.getStatus())) {
                response.put("status", "error");
                response.put("message", "Only completed production plans can be packed");
                return ResponseEntity.badRequest().body(response);
            }

            packingData.setRollingPlan(plan);
            packingData.setStatus("PACKED");

            // Auto-fill PO No and Customer from LOI if missing
            if ((packingData.getPoNo() == null || packingData.getPoNo().isBlank()) && plan.getLoi() != null) {
                packingData.setPoNo(plan.getLoi().getLoiNumber());
            }
            if ((packingData.getCustomer() == null || packingData.getCustomer().isBlank()) && plan.getLoi() != null) {
                packingData.setCustomer(plan.getLoi().getCustomerName());
            }

            PackingEntity saved = packingRepository.save(packingData);

            response.put("status", "success");
            response.put("message", "Packing created successfully!");
            response.put("packingId", saved.getId());
            return ResponseEntity.ok(response);
        }).orElseGet(() -> {
            response.put("status", "error");
            response.put("message", "Production plan not found");
            return ResponseEntity.notFound().build();
        });
    }

    // ✅ Update existing packing
    @PutMapping("/{id}")
    public ResponseEntity<PackingEntity> updatePacking(@PathVariable Long id, @RequestBody PackingEntity packing) {
        return packingRepository.findById(id).map(existing -> {
            existing.setSrNo(packing.getSrNo());
            existing.setPoNo(packing.getPoNo());
            existing.setGrade(packing.getGrade());
            existing.setColourCode(packing.getColourCode());
            existing.setGradeSection(packing.getGradeSection());
            existing.setSectionWt(packing.getSectionWt());
            existing.setLength(packing.getLength());
            existing.setNoOfPcs(packing.getNoOfPcs());
            existing.setQtyInMt(packing.getQtyInMt());
            existing.setHeatNo(packing.getHeatNo());
            existing.setChallanQty(packing.getChallanQty());
            existing.setChallanNo(packing.getChallanNo());
            existing.setCustomer(packing.getCustomer());
            existing.setDocNo(packing.getDocNo());
            existing.setPackingDate(packing.getPackingDate());
            existing.setLorryNo(packing.getLorryNo());
            return ResponseEntity.ok(packingRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ Delete packing
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePacking(@PathVariable Long id) {
        packingRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    // ✅ Update status (PACKED, DISPATCHED, etc.)
    @PutMapping("/{id}/status")
    public ResponseEntity<PackingEntity> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return packingRepository.findById(id).map(existing -> {
            existing.setStatus(status);
            return ResponseEntity.ok(packingRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ✅ Auto-create dispatch from packing
    @PostMapping("/{packingId}/auto-dispatch")
    @Transactional
    public ResponseEntity<Map<String, Object>> autoCreateDispatch(
            @PathVariable Long packingId,
            @RequestParam(required = false) String vehicleNo,
            @RequestParam(required = false) String fromLocation,
            @RequestParam(required = false) String destination) {

        Map<String, Object> response = new HashMap<>();

        return packingRepository.findById(packingId).map(packing -> {
            if (!"PACKED".equalsIgnoreCase(packing.getStatus())) {
                response.put("status", "error");
                response.put("message", "Only PACKED items can be dispatched");
                return ResponseEntity.badRequest().body(response);
            }

            DispatchEntity dispatch = new DispatchEntity();
            dispatch.setDispatchId("DISP-" + System.currentTimeMillis());
            dispatch.setPoNo(packing.getPoNo());
            dispatch.setCustomer(packing.getCustomer());
            dispatch.setSection(packing.getGradeSection());
            dispatch.setQtyIssued(packing.getQtyInMt());
            dispatch.setChallanNo(packing.getChallanNo());
            dispatch.setVehicleNo(vehicleNo != null ? vehicleNo : "");
            dispatch.setFromLocation(fromLocation != null ? fromLocation : "");
            dispatch.setDestination(destination != null ? destination : "");
            dispatch.setDispatchDate(LocalDate.now());
            dispatch.setStatus("Pending");

            DispatchEntity savedDispatch = dispatchRepository.save(dispatch);

            packing.setStatus("DISPATCHED");
            packing.setDispatch(savedDispatch);
            packingRepository.save(packing);

            response.put("status", "success");
            response.put("message", "Dispatch created successfully!");
            response.put("dispatchId", savedDispatch.getDispatchId());
            return ResponseEntity.ok(response);
        }).orElseGet(() -> {
            response.put("status", "error");
            response.put("message", "Packing record not found");
            return ResponseEntity.notFound().build();
        });
    }
}
