package com.ERP.Vinar.services;

import com.ERP.Vinar.entities.DispatchEntity;
import com.ERP.Vinar.entities.LoiEntity;
import com.ERP.Vinar.entities.PurchaseOrdersEntity;
import com.ERP.Vinar.repositories.DispatchRepository;
import com.ERP.Vinar.repositories.LoiRepository;
import com.ERP.Vinar.repositories.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class NumberSeriesService {

    @Autowired private LoiRepository loiRepository;
    @Autowired private PurchaseOrderRepository poRepository;
    @Autowired private DispatchRepository dispatchRepository;

    private static final int START_SEQ = 1001;

    public synchronized String nextLoiNumber() {
        String year = String.valueOf(LocalDate.now().getYear());
        String prefix = "LOI-" + year + "-";
        Optional<LoiEntity> top = loiRepository.findTopByLoiNumberStartingWithOrderByLoiNumberDesc(prefix);
        int next = nextFromTop(top.map(LoiEntity::getLoiNumber).orElse(null), prefix);
        return prefix + String.format("%04d", next);
    }

    public synchronized String nextPurchaseOrderNumber() {
        String year = String.valueOf(LocalDate.now().getYear());
        String prefix = "PO-" + year + "-";
        Optional<PurchaseOrdersEntity> top = poRepository.findTopByOrderNoStartingWithOrderByOrderNoDesc(prefix);
        int next = nextFromTop(top.map(PurchaseOrdersEntity::getOrderNo).orElse(null), prefix);
        return prefix + String.format("%04d", next);
    }

    public synchronized String nextChallanNumber() {
        String year = String.valueOf(LocalDate.now().getYear());
        String prefix = "CH-" + year + "-";
        Optional<DispatchEntity> top = dispatchRepository.findTopByChallanNoStartingWithOrderByChallanNoDesc(prefix);
        int next = nextFromTop(top.map(DispatchEntity::getChallanNo).orElse(null), prefix);
        return prefix + String.format("%04d", next);
    }

    private int nextFromTop(String lastNumber, String prefix) {
        if (lastNumber == null || lastNumber.isBlank()) return START_SEQ;
        try {
            String seqStr = lastNumber.replace(prefix, "");
            int seq = Integer.parseInt(seqStr);
            return seq + 1;
        } catch (Exception e) {
            // Fallback to start if parsing fails
            return START_SEQ;
        }
    }
}
