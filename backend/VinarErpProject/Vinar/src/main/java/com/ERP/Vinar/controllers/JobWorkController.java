package com.ERP.Vinar.controllers;

import com.ERP.Vinar.entities.JobWorkEntity;
import com.ERP.Vinar.repositories.JobWorkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/job-work")
@CrossOrigin(origins = "http://localhost:4200")
public class JobWorkController {

    @Autowired
    private JobWorkRepository jobWorkRepository;

    @GetMapping
    public ResponseEntity<List<JobWorkEntity>> getAllJobWork() {
        return ResponseEntity.ok(jobWorkRepository.findAll());
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<JobWorkEntity>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(jobWorkRepository.findByStatus(status));
    }

    @GetMapping("/vendor/{vendorId}")
    public ResponseEntity<List<JobWorkEntity>> getByVendor(@PathVariable String vendorId) {
        return ResponseEntity.ok(jobWorkRepository.findByVendorId(vendorId));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createJobWork(@RequestBody JobWorkEntity jobWork) {
        Map<String, Object> response = new HashMap<>();
        
        // Auto-generate jobwork ID
        jobWork.setJobworkId("JW-" + System.currentTimeMillis());
        jobWork.setStatus("Sent");
        jobWork.setBillingStatus("Pending");
        
        JobWorkEntity saved = jobWorkRepository.save(jobWork);
        
        response.put("status", "success");
        response.put("message", "Job work order created successfully!");
        response.put("jobworkId", saved.getJobworkId());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobWorkEntity> updateJobWork(@PathVariable Long id, @RequestBody JobWorkEntity jobWork) {
        return jobWorkRepository.findById(id).map(existing -> {
            existing.setVendorName(jobWork.getVendorName());
            existing.setVendorContact(jobWork.getVendorContact());
            existing.setOperation(jobWork.getOperation());
            existing.setMaterialDescription(jobWork.getMaterialDescription());
            existing.setMaterialSentQty(jobWork.getMaterialSentQty());
            existing.setMaterialReceivedQty(jobWork.getMaterialReceivedQty());
            existing.setDateSent(jobWork.getDateSent());
            existing.setDateReceived(jobWork.getDateReceived());
            existing.setExpectedReturnDate(jobWork.getExpectedReturnDate());
            existing.setStatus(jobWork.getStatus());
            existing.setBillingAmount(jobWork.getBillingAmount());
            existing.setBillingStatus(jobWork.getBillingStatus());
            existing.setRemarks(jobWork.getRemarks());
            return ResponseEntity.ok(jobWorkRepository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/receive")
    public ResponseEntity<Map<String, Object>> receiveMaterial(
            @PathVariable Long id,
            @RequestParam Double receivedQty) {
        
        Map<String, Object> response = new HashMap<>();
        
        return jobWorkRepository.findById(id).map(jobWork -> {
            jobWork.setMaterialReceivedQty(receivedQty);
            jobWork.setDateReceived(java.time.LocalDate.now());
            jobWork.setStatus("Received");
            
            jobWorkRepository.save(jobWork);
            
            response.put("status", "success");
            response.put("message", "Material received successfully!");
            return ResponseEntity.ok(response);
        }).orElseGet(() -> {
            response.put("status", "error");
            response.put("message", "Job work not found");
            return ResponseEntity.notFound().build();
        });
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJobWork(@PathVariable Long id) {
        jobWorkRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
