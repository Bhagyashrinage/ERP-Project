package com.ERP.Vinar.service;

import com.ERP.Vinar.dto.JobWorkChallanDTO;
import com.ERP.Vinar.entities.JobWorkChallan;

import java.util.List;

public interface JobWorkChallanService {
    
    JobWorkChallanDTO createChallan(JobWorkChallanDTO challanDTO);
    
    JobWorkChallanDTO updateChallan(Long id, JobWorkChallanDTO challanDTO);
    
    JobWorkChallanDTO getChallanById(Long id);
    
    List<JobWorkChallanDTO> searchChallans(String challanNo, String supplier, JobWorkChallan.JobWorkStatus status);
    
    void deleteChallan(Long id);
    
    JobWorkChallanDTO updateChallanStatus(Long id, JobWorkChallan.JobWorkStatus status);
}
