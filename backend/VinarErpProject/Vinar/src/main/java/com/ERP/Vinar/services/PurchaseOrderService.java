package com.ERP.Vinar.services;

import com.ERP.Vinar.entities.PurchaseOrdersEntity;
import com.ERP.Vinar.repositories.PurchaseOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PurchaseOrderService {

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    public List<PurchaseOrdersEntity> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    public PurchaseOrdersEntity getPurchaseOrderById(Long id) {
        return purchaseOrderRepository.findById(id).orElse(null);
    }

    public PurchaseOrdersEntity createPurchaseOrder(PurchaseOrdersEntity purchaseOrder) {
        return purchaseOrderRepository.save(purchaseOrder);
    }

    public PurchaseOrdersEntity updatePurchaseOrder(Long id, PurchaseOrdersEntity purchaseOrderDetails) {
        PurchaseOrdersEntity purchaseOrder = purchaseOrderRepository.findById(id).orElse(null);
        if (purchaseOrder != null) {
            purchaseOrder.setOrderNo(purchaseOrderDetails.getOrderNo());
            purchaseOrder.setDate(purchaseOrderDetails.getDate());
            purchaseOrder.setStatus(purchaseOrderDetails.getStatus());
            purchaseOrder.setVendor(purchaseOrderDetails.getVendor());
            purchaseOrder.setLocation(purchaseOrderDetails.getLocation());
            purchaseOrder.setElements(purchaseOrderDetails.getElements());
            return purchaseOrderRepository.save(purchaseOrder);
        }
        return null;
    }

    public void deletePurchaseOrder(Long id) {
        purchaseOrderRepository.deleteById(id);
    }
}
