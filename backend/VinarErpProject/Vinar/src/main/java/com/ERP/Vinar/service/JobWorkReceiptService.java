package com.ERP.Vinar.service;

import com.ERP.Vinar.dto.JobWorkReceiptDTO;
import com.ERP.Vinar.entities.JobWorkReceipt;

import java.util.List;

public interface JobWorkReceiptService {
    
    JobWorkReceiptDTO createReceipt(JobWorkReceiptDTO receiptDTO);
    
    JobWorkReceiptDTO updateReceipt(Long id, JobWorkReceiptDTO receiptDTO);
    
    JobWorkReceiptDTO getReceiptById(Long id);
    
    List<JobWorkReceiptDTO> searchReceipts(String challanNo, JobWorkReceipt.ReceiptStatus status);
    
    JobWorkReceiptDTO approveReceipt(Long id, String approvedBy);
    
    JobWorkReceiptDTO rejectReceipt(Long id, String rejectedBy, String remarks);
    
    void deleteReceipt(Long id);
}
