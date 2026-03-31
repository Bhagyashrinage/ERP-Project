package com.ERP.Vinar.service.impl;

import com.ERP.Vinar.dto.JobWorkChallanDTO;
import com.ERP.Vinar.entities.JobWorkChallan;
import com.ERP.Vinar.exception.ResourceNotFoundException;
import com.ERP.Vinar.repository.JobWorkChallanRepository;
import com.ERP.Vinar.service.JobWorkChallanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JobWorkChallanServiceImpl implements JobWorkChallanService {

    private final JobWorkChallanRepository challanRepository;

    @Override
    @Transactional
    public JobWorkChallanDTO createChallan(JobWorkChallanDTO challanDTO) {
        // Check if challan with same number already exists
        if (challanRepository.existsByChallanNo(challanDTO.getChallanNo())) {
            throw new IllegalArgumentException("Challan with number " + challanDTO.getChallanNo() + " already exists");
        }

        JobWorkChallan challan = challanDTO.toEntity();
        challan = challanRepository.save(challan);
        return JobWorkChallanDTO.fromEntity(challan);
    }

    @Override
    @Transactional
    public JobWorkChallanDTO updateChallan(Long id, JobWorkChallanDTO challanDTO) {
        JobWorkChallan existingChallan = challanRepository.findByIdWithLines(id)
                .orElseThrow(() -> new ResourceNotFoundException("Challan not found with id: " + id));

        // Update the existing challan with new data
        challanDTO.updateEntity(existingChallan);
        
        // Save the updated challan
        JobWorkChallan updatedChallan = challanRepository.save(existingChallan);
        return JobWorkChallanDTO.fromEntity(updatedChallan);
    }

    @Override
    @Transactional(readOnly = true)
    public JobWorkChallanDTO getChallanById(Long id) {
        return challanRepository.findByIdWithLines(id)
                .map(JobWorkChallanDTO::fromEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Challan not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<JobWorkChallanDTO> searchChallans(String challanNo, String supplier, JobWorkChallan.JobWorkStatus status) {
        return challanRepository.search(
                challanNo != null ? challanNo : "",
                supplier != null ? supplier : "",
                status
        ).stream()
        .map(JobWorkChallanDTO::fromEntity)
        .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteChallan(Long id) {
        JobWorkChallan challan = challanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Challan not found with id: " + id));
        
        // Check if there are any associated receipts
        if (!challan.getReceipts().isEmpty()) {
            throw new IllegalStateException("Cannot delete challan with associated receipts");
        }
        
        challanRepository.delete(challan);
    }

    @Override
    @Transactional
    public JobWorkChallanDTO updateChallanStatus(Long id, JobWorkChallan.JobWorkStatus status) {
        JobWorkChallan challan = challanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Challan not found with id: " + id));
        
        // Validate status transition
        validateStatusTransition(challan.getStatus(), status);
        
        challan.setStatus(status);
        challan = challanRepository.save(challan);
        return JobWorkChallanDTO.fromEntity(challan);
    }
    
    private void validateStatusTransition(JobWorkChallan.JobWorkStatus currentStatus, JobWorkChallan.JobWorkStatus newStatus) {
        // Add validation logic for status transitions if needed
        // For example, you might want to prevent moving from COMPLETED back to ISSUED
    }
}
