package com.ERP.Vinar.service.impl;

import com.ERP.Vinar.dto.JobWorkReceiptDTO;
import com.ERP.Vinar.entities.JobWorkChallan;
import com.ERP.Vinar.entities.JobWorkReceipt;
import com.ERP.Vinar.exception.ResourceNotFoundException;
import com.ERP.Vinar.repository.JobWorkChallanRepository;
import com.ERP.Vinar.repository.JobWorkReceiptRepository;
import com.ERP.Vinar.service.JobWorkReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobWorkReceiptServiceImpl implements JobWorkReceiptService {

    private final JobWorkReceiptRepository receiptRepository;
    private final JobWorkChallanRepository challanRepository;

    @Override
    @Transactional
    public JobWorkReceiptDTO createReceipt(JobWorkReceiptDTO receiptDTO) {
        // Find the associated challan
        JobWorkChallan challan = challanRepository.findById(receiptDTO.getChallanId())
                .orElseThrow(() -> new ResourceNotFoundException("Challan not found with id: " + receiptDTO.getChallanId()));
        
        // Allow multiple receipts per challan (removed unique constraint check)
        
        // Create and save the receipt
        JobWorkReceipt receipt = receiptDTO.toEntity();
        receipt.setChallan(challan);
        receipt.setChallanNo(challan.getChallanNo());
        
        // Calculate difference if not provided
        if (receipt.getDifference() == null) {
            receipt.setDifference(receipt.getActualWeight() - receipt.getKataWeight());
        }
        
        receipt = receiptRepository.save(receipt);
        
        // Update challan status if needed
        if (challan.getStatus() == JobWorkChallan.JobWorkStatus.ISSUED) {
            challan.setStatus(JobWorkChallan.JobWorkStatus.PENDING_RETURN);
            challanRepository.save(challan);
        }
        
        return JobWorkReceiptDTO.fromEntity(receipt);
    }

    @Override
    @Transactional
    public JobWorkReceiptDTO updateReceipt(Long id, JobWorkReceiptDTO receiptDTO) {
        JobWorkReceipt existingReceipt = receiptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found with id: " + id));
        
        // Only allow updates to certain fields
        existingReceipt.setReceiptDate(receiptDTO.getReceiptDate());
        existingReceipt.setQtyReceived(receiptDTO.getQtyReceived());
        existingReceipt.setActualWeight(receiptDTO.getActualWeight());
        existingReceipt.setKataWeight(receiptDTO.getKataWeight());
        existingReceipt.setDifference(receiptDTO.getActualWeight() - receiptDTO.getKataWeight());
        existingReceipt.setJobCharge(receiptDTO.getJobCharge());
        existingReceipt.setRemarks(receiptDTO.getRemarks());
        
        // Only allow status change if not already approved/rejected
        if (existingReceipt.getStatus() == JobWorkReceipt.ReceiptStatus.PENDING) {
            existingReceipt.setStatus(receiptDTO.getStatus());
        }
        
        existingReceipt = receiptRepository.save(existingReceipt);
        return JobWorkReceiptDTO.fromEntity(existingReceipt);
    }

    @Override
    @Transactional(readOnly = true)
    public JobWorkReceiptDTO getReceiptById(Long id) {
        return receiptRepository.findByIdWithChallanAndLines(id)
                .map(JobWorkReceiptDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobWorkReceiptDTO> searchReceipts(String challanNo, JobWorkReceipt.ReceiptStatus status) {
        return receiptRepository.search(
                challanNo != null ? challanNo : "",
                status
        ).stream()
        .map(JobWorkReceiptDTO::fromEntity)
        .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public JobWorkReceiptDTO approveReceipt(Long id, String approvedBy) {
        JobWorkReceipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found with id: " + id));
        
        if (receipt.getStatus() != JobWorkReceipt.ReceiptStatus.PENDING) {
            throw new IllegalStateException("Only pending receipts can be approved");
        }
        
        receipt.setStatus(JobWorkReceipt.ReceiptStatus.APPROVED);
        receipt.setApprovedAt(LocalDate.now());
        receipt.setApprovedBy(approvedBy);
        
        // Update challan status to COMPLETED
        JobWorkChallan challan = receipt.getChallan();
        challan.setStatus(JobWorkChallan.JobWorkStatus.COMPLETED);
        challanRepository.save(challan);
        
        receipt = receiptRepository.save(receipt);
        return JobWorkReceiptDTO.fromEntity(receipt);
    }

    @Override
    @Transactional
    public JobWorkReceiptDTO rejectReceipt(Long id, String rejectedBy, String remarks) {
        JobWorkReceipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found with id: " + id));
        
        if (receipt.getStatus() != JobWorkReceipt.ReceiptStatus.PENDING) {
            throw new IllegalStateException("Only pending receipts can be rejected");
        }
        
        receipt.setStatus(JobWorkReceipt.ReceiptStatus.REJECTED);
        receipt.setApprovedAt(LocalDate.now());
        receipt.setApprovedBy(rejectedBy);
        receipt.setRemarks(remarks);
        
        // Update challan status back to ISSUED
        JobWorkChallan challan = receipt.getChallan();
        challan.setStatus(JobWorkChallan.JobWorkStatus.ISSUED);
        challanRepository.save(challan);
        
        receipt = receiptRepository.save(receipt);
        return JobWorkReceiptDTO.fromEntity(receipt);
    }

    @Override
    @Transactional
    public void deleteReceipt(Long id) {
        JobWorkReceipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found with id: " + id));
        
        if (receipt.getStatus() != JobWorkReceipt.ReceiptStatus.PENDING) {
            throw new IllegalStateException("Only pending receipts can be deleted");
        }
        
        // Update challan status back to ISSUED
        JobWorkChallan challan = receipt.getChallan();
        if (challan.getStatus() == JobWorkChallan.JobWorkStatus.PENDING_RETURN) {
            challan.setStatus(JobWorkChallan.JobWorkStatus.ISSUED);
            challanRepository.save(challan);
        }
        
        receiptRepository.delete(receipt);
    }
}
