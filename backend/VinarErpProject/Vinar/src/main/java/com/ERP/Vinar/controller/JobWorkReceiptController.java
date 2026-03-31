package com.ERP.Vinar.controller;

import com.ERP.Vinar.dto.JobWorkReceiptDTO;
import com.ERP.Vinar.entities.JobWorkReceipt;
import com.ERP.Vinar.service.JobWorkReceiptService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/jobwork/receipts")
public class JobWorkReceiptController {

    private final JobWorkReceiptService receiptService;

    public JobWorkReceiptController(JobWorkReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    @PostMapping
    public ResponseEntity<?> createReceipt(@Valid @RequestBody JobWorkReceiptDTO receiptDTO, BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> 
                errors.put(error.getField(), error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errors);
        }
        
        try {
            JobWorkReceiptDTO createdReceipt = receiptService.createReceipt(receiptDTO);
            return ResponseEntity.ok(createdReceipt);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            errorResponse.put("type", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobWorkReceiptDTO> updateReceipt(
            @PathVariable Long id, 
            @Valid @RequestBody JobWorkReceiptDTO receiptDTO) {
        
        JobWorkReceiptDTO updatedReceipt = receiptService.updateReceipt(id, receiptDTO);
        return ResponseEntity.ok(updatedReceipt);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobWorkReceiptDTO> getReceiptById(@PathVariable Long id) {
        JobWorkReceiptDTO receipt = receiptService.getReceiptById(id);
        return ResponseEntity.ok(receipt);
    }

    @GetMapping
    public ResponseEntity<List<JobWorkReceiptDTO>> searchReceipts(
            @RequestParam(required = false) String challanNo,
            @RequestParam(required = false) JobWorkReceipt.ReceiptStatus status) {
        
        List<JobWorkReceiptDTO> receipts = receiptService.searchReceipts(challanNo, status);
        return ResponseEntity.ok(receipts);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<JobWorkReceiptDTO> approveReceipt(
            @PathVariable Long id,
            @RequestParam String approvedBy) {
        
        JobWorkReceiptDTO approvedReceipt = receiptService.approveReceipt(id, approvedBy);
        return ResponseEntity.ok(approvedReceipt);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<JobWorkReceiptDTO> rejectReceipt(
            @PathVariable Long id,
            @RequestParam String rejectedBy,
            @RequestParam(required = false) String remarks) {
        
        JobWorkReceiptDTO rejectedReceipt = receiptService.rejectReceipt(id, rejectedBy, remarks);
        return ResponseEntity.ok(rejectedReceipt);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReceipt(@PathVariable Long id) {
        receiptService.deleteReceipt(id);
        return ResponseEntity.noContent().build();
    }
}
