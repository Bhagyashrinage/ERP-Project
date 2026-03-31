package com.ERP.Vinar.controllers;

import com.ERP.Vinar.entities.DispatchEntity;
import com.ERP.Vinar.repositories.DispatchRepository;
import com.ERP.Vinar.repositories.PackingRepository;
import com.ERP.Vinar.services.NumberSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dispatch")
@CrossOrigin(origins = "http://localhost:4200")
public class DispatchController {

    @Autowired
    private DispatchRepository dispatchRepository;
    
    @Autowired
    private PackingRepository packingRepository;

    @Autowired
    private NumberSeriesService numberSeriesService;

    // ✅ Next challan number for FE prefill
    @GetMapping("/next-challan")
    public Map<String, String> getNextChallan() {
        Map<String, String> res = new HashMap<>();
        res.put("number", numberSeriesService.nextChallanNumber());
        return res;
    }

    @GetMapping
    public ResponseEntity<List<DispatchEntity>> getAllDispatches() {
        return ResponseEntity.ok(dispatchRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<DispatchEntity> createDispatch(@RequestBody DispatchEntity dispatch) {
        if (dispatch.getChallanNo() == null || dispatch.getChallanNo().isBlank()) {
            dispatch.setChallanNo(numberSeriesService.nextChallanNumber());
        }
        DispatchEntity saved = dispatchRepository.save(dispatch);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DispatchEntity> updateDispatch(@PathVariable Long id, @RequestBody DispatchEntity dispatch) {
        return dispatchRepository.findById(id).map(existing -> {
            existing.setInvoiceNo(dispatch.getInvoiceNo());
            existing.setInvoiceDate(dispatch.getInvoiceDate());
            existing.setSection(dispatch.getSection());
            existing.setCustomer(dispatch.getCustomer());
            existing.setQtyIssued(dispatch.getQtyIssued());
            existing.setVehicleNo(dispatch.getVehicleNo());
            existing.setJobworkNo(dispatch.getJobworkNo());
            existing.setChallanNo(dispatch.getChallanNo());
            existing.setPoNo(dispatch.getPoNo());
            existing.setPoOrderDate(dispatch.getPoOrderDate());
            existing.setFromLocation(dispatch.getFromLocation());
            existing.setDestination(dispatch.getDestination());
            existing.setStatus(dispatch.getStatus());
            existing.setDispatchDate(dispatch.getDispatchDate());
            return ResponseEntity.ok(dispatchRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDispatch(@PathVariable Long id) {
        dispatchRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/from-packing/{packingId}")
    public ResponseEntity<DispatchEntity> getDispatchByPackingId(@PathVariable Long packingId) {
        return packingRepository.findById(packingId)
                .map(packing -> packing.getDispatch() != null
                        ? ResponseEntity.ok(packing.getDispatch())
                        : ResponseEntity.status(HttpStatus.NOT_FOUND).<DispatchEntity>build())
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).<DispatchEntity>build());
    }

    /**
     * ✅ Create Dispatch from Packing Entry
     */
    @PostMapping("/from-packing/{packingId}")
    @Transactional
    public ResponseEntity<Map<String, Object>> createDispatchFromPacking(
            @PathVariable Long packingId,
            @RequestBody DispatchEntity dispatchData) {
        
        Map<String, Object> response = new HashMap<>();
        
        return packingRepository.findById(packingId).map(packing -> {
            if (!"PACKED".equals(packing.getStatus())) {
                response.put("status", "error");
                response.put("message", "Only PACKED items can be dispatched");
                return ResponseEntity.badRequest().body(response);
            }
            
            // Link dispatch to packing
            dispatchData.setPacking(packing);
            dispatchData.setStatus("Pending");
            if (dispatchData.getChallanNo() == null || dispatchData.getChallanNo().isBlank()) {
                dispatchData.setChallanNo(numberSeriesService.nextChallanNumber());
            }
            
            DispatchEntity saved = dispatchRepository.save(dispatchData);
            
            // Update packing status
            packing.setStatus("DISPATCHED");
            packingRepository.save(packing);
            
            response.put("status", "success");
            response.put("message", "Dispatch created successfully!");
            response.put("dispatchId", saved.getDispatchId());
            response.put("challanNo", saved.getChallanNo());
            return ResponseEntity.ok(response);
        }).orElseGet(() -> {
            response.put("status", "error");
            response.put("message", "Packing item not found");
            return ResponseEntity.notFound().build();
        });
    }
}
