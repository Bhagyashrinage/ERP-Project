package com.ERP.Vinar.controllers;

import com.ERP.Vinar.dto.GRNCreateRequest;
import com.ERP.Vinar.dto.GRNDTO;
import com.ERP.Vinar.dto.GRNItemUpdateRequest;
import com.ERP.Vinar.entities.*;
import com.ERP.Vinar.repositories.GRNRepository;
import com.ERP.Vinar.repositories.PurchaseOrderRepository;
import com.ERP.Vinar.repositories.RMStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/grn")
@CrossOrigin(origins = "http://localhost:4200")
public class GRNController {

    @Autowired
    private GRNRepository grnRepo;

    @Autowired
    private PurchaseOrderRepository poRepo;

    @Autowired
    private RMStockRepository stockRepo;

    // ✅ Fetch all GRNs
    @GetMapping
    public List<GRNDTO> getAllGRNs() {
        return grnRepo.findAll().stream()
                .map(GRNDTO::fromEntity)
                .toList();
    }

    // ✅ Fetch single GRN
    @GetMapping("/{grnId}")
    public GRNDTO getGRN(@PathVariable Long grnId) {
        GRNEntity grn = grnRepo.findById(grnId)
                .orElseThrow(() -> new RuntimeException("GRN not found with ID: " + grnId));
        return GRNDTO.fromEntity(grn);
    }

    // ✅ Create GRN for a given PO
    @PostMapping("/po/{poId}")
    @Transactional
    public GRNDTO createGRN(@PathVariable Long poId, @RequestBody GRNCreateRequest grnRequest) {

        PurchaseOrdersEntity po = poRepo.findById(poId)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found with ID: " + poId));

        GRNEntity grn = new GRNEntity();
        grn.setPo(po);
        grn.setLocation(po.getLocation());
        grn.setGrnNumber(grnRequest.getGrnNumber() != null ? grnRequest.getGrnNumber() : "GRN-" + System.currentTimeMillis());
        grn.setVehicleNo(grnRequest.getVehicleNo() != null ? grnRequest.getVehicleNo() : "");
        grn.setGrnDate(grnRequest.getGrnDate() != null ? grnRequest.getGrnDate() : LocalDate.now());
        grn.setReceiverName(grnRequest.getReceiverName() != null ? grnRequest.getReceiverName() : "");

        List<GRNItemEntity> grnItems = new ArrayList<>();

        if (grnRequest.getItems() != null && !grnRequest.getItems().isEmpty()) {
            for (GRNItemUpdateRequest itemReq : grnRequest.getItems()) {

                if (itemReq.getPoElementId() == null)
                    throw new IllegalArgumentException("Missing poElementId for one of the GRN items.");

                PurchaseOrderElements poElem = po.getElements().stream()
                        .filter(e -> e.getId().equals(itemReq.getPoElementId()))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Invalid PO Element ID: " + itemReq.getPoElementId()));

                BigDecimal receivedQty = normalize(itemReq.getReceivedQty());
                BigDecimal alreadyReceived = normalize(poElem.getReceivedQuantity());
                BigDecimal remaining = normalize(poElem.getQuantity()).subtract(alreadyReceived);

                if (receivedQty.compareTo(remaining) > 0)
                    throw new IllegalArgumentException("Received quantity exceeds remaining for PO Element ID: " + poElem.getId());

                // Create GRN item
                GRNItemEntity grnItem = new GRNItemEntity();
                grnItem.setPoElement(poElem);
                grnItem.setGrn(grn);
                grnItem.setReceivedQuantity(receivedQty);
                grnItem.setRemarks(itemReq.getRemarks());
                grnItems.add(grnItem);

                // Update PO element
                poElem.setReceivedQuantity(alreadyReceived.add(receivedQty));

                // ✅ Update Stock
                updateStockFromGRN(po.getLocation(), poElem, receivedQty);
            }
        }

        grn.setItems(grnItems);

        boolean allReceived = po.getElements().stream()
                .allMatch(e -> normalize(e.getReceivedQuantity()).compareTo(normalize(e.getQuantity())) >= 0);
        po.setStatus(allReceived ? "Received" : "Partially Received");

        GRNEntity savedGRN = grnRepo.save(grn);
        poRepo.save(po);

        return GRNDTO.fromEntity(savedGRN);
    }

    // ✅ Update stock by (material, grade, section, steelWidth, length)
    private void updateStockFromGRN(String location, PurchaseOrderElements elem, BigDecimal receivedQty) {
        RMStockEntity stock = stockRepo
                .findByLocationAndMaterialAndGradeAndSectionAndSteelWidthAndLengthAndType(
                        location,
                        elem.getMaterial(),
                        elem.getGrade(),
                        elem.getSection(),
                        elem.getSteelWidth(),
                        elem.getLength(),
                        elem.getType()
                )
                .orElseGet(() -> {
                    RMStockEntity s = new RMStockEntity();
                    s.setLocation(location);
                    s.setMaterial(elem.getMaterial());
                    s.setGrade(elem.getGrade());
                    s.setSection(elem.getSection());
                    s.setSteelWidth(elem.getSteelWidth());
                    s.setLength(elem.getLength());
                    s.setType(elem.getType());
                    s.setTotalQuantity(BigDecimal.ZERO);
                    return s;
                });

        stock.setTotalQuantity(stock.getTotalQuantity().add(receivedQty));
        stock.setLastUpdated(LocalDateTime.now());
        stockRepo.save(stock);
    }

    private BigDecimal normalize(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value.setScale(3, RoundingMode.HALF_UP);
    }
}
