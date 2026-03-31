package com.ERP.Vinar.controller;

import com.ERP.Vinar.dto.JobWorkChallanDTO;
import com.ERP.Vinar.entities.JobWorkChallan;
import com.ERP.Vinar.service.JobWorkChallanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobwork/challans")
public class JobWorkChallanController {

    private final JobWorkChallanService challanService;

    public JobWorkChallanController(JobWorkChallanService challanService) {
        this.challanService = challanService;
    }

    @PostMapping
    public ResponseEntity<?> createChallan(@Valid @RequestBody JobWorkChallanDTO challanDTO, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> 
                errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        
        try {
            JobWorkChallanDTO createdChallan = challanService.createChallan(challanDTO);
            return ResponseEntity.ok(createdChallan);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobWorkChallanDTO> updateChallan(
            @PathVariable Long id, 
            @Valid @RequestBody JobWorkChallanDTO challanDTO) {
        JobWorkChallanDTO updatedChallan = challanService.updateChallan(id, challanDTO);
        return ResponseEntity.ok(updatedChallan);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobWorkChallanDTO> getChallanById(@PathVariable Long id) {
        JobWorkChallanDTO challan = challanService.getChallanById(id);
        return ResponseEntity.ok(challan);
    }

    @GetMapping
    public ResponseEntity<List<JobWorkChallanDTO>> searchChallans(
            @RequestParam(required = false) String challanNo,
            @RequestParam(required = false) String supplier,
            @RequestParam(required = false) JobWorkChallan.JobWorkStatus status) {
        
        List<JobWorkChallanDTO> challans = challanService.searchChallans(challanNo, supplier, status);
        return ResponseEntity.ok(challans);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChallan(@PathVariable Long id) {
        challanService.deleteChallan(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<JobWorkChallanDTO> updateChallanStatus(
            @PathVariable Long id,
            @RequestParam JobWorkChallan.JobWorkStatus status) {
        
        JobWorkChallanDTO updatedChallan = challanService.updateChallanStatus(id, status);
        return ResponseEntity.ok(updatedChallan);
    }
}
