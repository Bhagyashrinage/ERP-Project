package com.ERP.Vinar.controllers;

import com.ERP.Vinar.dto.*;
import com.ERP.Vinar.entities.*;
import com.ERP.Vinar.repositories.*;
import java.time.LocalDateTime;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.*;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rolling-plan")
@CrossOrigin(origins = "http://localhost:4200")  // ✅ Ensure Angular access
public class RollingPlanController {

    @Autowired private RollingPlanRepository planRepo;
    @Autowired private RollingPlanItemRepository itemRepo;
    @Autowired private LoiRepository loiRepo;
    @Autowired private RMStockRepository stockRepo;
    @Autowired private LoiItemRepository loiItemRepo;
    @Autowired private PackingRepository packingRepo;

    /**
     * ✅ Create Rolling Plan from selected stock items (ignore material)
     */
    @PostMapping("/from-selection")
    @Transactional
    public Map<String, Object> createPlanFromSelection(@RequestBody RollingPlanSelectionDTO dto) {

        Map<String, Object> response = new HashMap<>();
        List<String> messages = new ArrayList<>();

        // --- Validate LOI ---
        LoiEntity loi = loiRepo.findById(dto.getLoiId())
                .orElseThrow(() -> new RuntimeException("LOI not found"));

        // --- Create Plan Header ---
        RollingPlanEntity plan = new RollingPlanEntity();
        plan.setPlanNumber("RP-" + LocalDate.now().getYear() + "-" + (System.currentTimeMillis() % 10000));
        plan.setPlanDate(LocalDate.now());
        plan.setLocation(loi.getLocation());
        plan.setMill(dto.getMill());
        plan.setShift(dto.getShift());
        plan.setStatus("Planned");
        plan.setLoi(loi);

        List<RollingPlanItemEntity> planItems = new ArrayList<>();
        boolean anyPlanned = false;

        // --- Loop through selected stocks for LOI items ---
        for (RollingPlanSelectionDTO.Selection s : dto.getSelectedStocks()) {

            LoiItemEntity loiItem = loiItemRepo.findById(s.getLoiItemId())
                    .orElseThrow(() -> new RuntimeException("LOI item not found"));

            RMStockEntity stock = stockRepo.findById(s.getStockId())
                    .orElseThrow(() -> new RuntimeException("Stock not found"));

            RollingPlanItemEntity planItem = new RollingPlanItemEntity();
            planItem.setMaterial(loiItem.getMaterial());
            planItem.setGrade(loiItem.getGrade());
            planItem.setSection(loiItem.getSection());
            planItem.setLength(loiItem.getLength().doubleValue());
            planItem.setUnit(loiItem.getUnit());
            planItem.setRollingPlan(plan);

            // BigDecimal required = loiItem.getQuantity();
            BigDecimal selectedQty = BigDecimal.valueOf(s.getPlannedQty());
            BigDecimal available = stock.getTotalQuantity();

            if (available.compareTo(selectedQty) >= 0) {
                // ✅ Enough stock for this selection
                planItem.setPlannedQty(selectedQty.doubleValue());
                planItem.setCompletedQty(0.0);
                planItem.setPendingQty(selectedQty.doubleValue());
                planItem.setRemark("Planned successfully from stock ID: " + stock.getId());

                // Update stock balance
                stock.setTotalQuantity(available.subtract(selectedQty));
                stock.setLastUpdated(java.time.LocalDateTime.now());
                stockRepo.save(stock);

                anyPlanned = true;
                messages.add("✅ " + loiItem.getSection() + " planned using stock " + stock.getId());
            } else {
                planItem.setPlannedQty(0.0);
                planItem.setCompletedQty(0.0);
                planItem.setPendingQty(0.0);
                planItem.setRemark("⚠️ Insufficient stock. Required: " + selectedQty + ", Available: " + available);
                messages.add("⚠️ " + loiItem.getSection() + " insufficient stock in stock " + stock.getId());
            }

            planItems.add(planItem);
        }

        if (!anyPlanned) {
            response.put("status", "failed");
            response.put("message", "No items could be planned due to insufficient stock.");
            return response;
        }

        // --- Save Plan ---
        plan.setItems(planItems);
        RollingPlanEntity savedPlan = planRepo.save(plan);

        // --- Update LOI Status ---
        loi.setStatus("Planned");
        loiRepo.save(loi);

        response.put("status", "success");
        response.put("planNumber", savedPlan.getPlanNumber());
        response.put("message", "Rolling Plan created successfully.");
        response.put("details", messages);
        return response;
    }

    /**
     * ✅ Get eligible stock for a specific LOI item (ignoring material)
     */
    @GetMapping("/eligible-stock/{loiItemId}")
    public List<RMStockEntity> getEligibleStock(@PathVariable Long loiItemId) {
        LoiItemEntity loiItem = loiItemRepo.findById(loiItemId)
                .orElseThrow(() -> new RuntimeException("LOI Item not found"));

        String location = loiItem.getLoi().getLocation();
        String grade = loiItem.getGrade() != null ? loiItem.getGrade().trim() : "";
        String section = loiItem.getSection() != null ? loiItem.getSection().trim() : "";
        BigDecimal lengthRequired = loiItem.getLength() != null ? loiItem.getLength() : BigDecimal.ZERO;

        System.out.println("\n=============================");
        System.out.println("🔍 Checking eligible stock for LOI Item ID: " + loiItemId);
        System.out.println("📍 Location: " + location);
        System.out.println("🧾 Grade: " + grade);
        System.out.println("📐 Section: " + section);
        System.out.println("📏 Min Length: " + lengthRequired);
        System.out.println("=============================");

        // First, get ALL stock to see what's available
        List<RMStockEntity> allStocks = stockRepo.findAll();
        System.out.println("🟦 Total stock items in database: " + allStocks.size());
        
        // Filter by location
        List<RMStockEntity> locationStocks = allStocks.stream()
                .filter(s -> s.getLocation() != null && s.getLocation().equalsIgnoreCase(location))
                .collect(Collectors.toList());
        System.out.println("📍 Stock items in location '" + location + "': " + locationStocks.size());

        // Very relaxed matching: show all stock in the location with any available quantity
        List<RMStockEntity> eligible = locationStocks.stream()
                .filter(s -> s.getTotalQuantity() != null && s.getTotalQuantity().compareTo(BigDecimal.ZERO) > 0)
                .collect(Collectors.toList());

        System.out.println("✅ Eligible Stock Count: " + eligible.size());
        eligible.forEach(s -> System.out.println("   → ID " + s.getId() + " | Location=" + s.getLocation() + 
                " | Section=" + s.getSection() + " | Grade=" + s.getGrade() + 
                " | Length=" + s.getLength() + " | Qty=" + s.getTotalQuantity()));

        return eligible;
    }
    
    /**
     * 🔍 Debug endpoint to check all stock
     */
    @GetMapping("/debug/all-stock")
    public List<RMStockEntity> getAllStockDebug() {
        List<RMStockEntity> allStocks = stockRepo.findAll();
        System.out.println("\n=============================");
        System.out.println("🔍 DEBUG: Total stock items: " + allStocks.size());
        allStocks.forEach(s -> System.out.println("   → ID " + s.getId() + " | Location=" + s.getLocation() + 
                " | Material=" + s.getMaterial() + " | Section=" + s.getSection() + 
                " | Grade=" + s.getGrade() + " | Qty=" + s.getTotalQuantity()));
        System.out.println("=============================\n");
        return allStocks;
    }



    // =================== Get All / Get One ===================

    @GetMapping
    public List<RollingPlanDTO> getAll() {
        return planRepo.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * ✅ Get only completed production items for packing
     */
    @GetMapping("/completed")
    public List<RollingPlanDTO> getCompletedPlans() {
        return planRepo.findByStatusIgnoreCaseAndPackingsIsEmpty("Completed").stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public RollingPlanDTO getById(@PathVariable Long id) {
        RollingPlanEntity entity = planRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Rolling Plan not found"));
        return convertToDTO(entity);
    }

    /**
     * ✅ Update Rolling Plan (including status)
     */
    @PutMapping("/{id}")
    @Transactional
    public RollingPlanDTO updatePlan(@PathVariable Long id, @RequestBody RollingPlanDTO dto) {
        RollingPlanEntity plan = planRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Rolling Plan not found"));
        
        plan.setStatus(dto.getStatus());
        plan.setMill(dto.getMill());
        plan.setShift(dto.getShift());
        
        RollingPlanEntity updated = planRepo.save(plan);
        return convertToDTO(updated);
    }

    // =================== DTO Conversions ===================

    private RollingPlanDTO convertToDTO(RollingPlanEntity plan) {
        RollingPlanDTO dto = new RollingPlanDTO();
        dto.setId(plan.getId());
        dto.setPlanNumber(plan.getPlanNumber());
        dto.setPlanDate(plan.getPlanDate());
        dto.setLocation(plan.getLocation());
        dto.setMill(plan.getMill());
        dto.setShift(plan.getShift());
        dto.setStatus(plan.getStatus());
        dto.setLoiId(plan.getLoi() != null ? plan.getLoi().getId() : null);
        dto.setLoiNumber(plan.getLoi() != null ? plan.getLoi().getLoiNumber() : null);
        dto.setCustomerName(plan.getLoi() != null ? plan.getLoi().getCustomerName() : null);

        if (plan.getItems() != null) {
            List<RollingPlanItemDTO> items = plan.getItems().stream()
                    .map(this::convertItemToDTO)
                    .collect(Collectors.toList());
            dto.setItems(items);
        }
        return dto;
    }

    private RollingPlanItemDTO convertItemToDTO(RollingPlanItemEntity item) {
        RollingPlanItemDTO dto = new RollingPlanItemDTO();
        dto.setMaterial(item.getMaterial());
        dto.setGrade(item.getGrade());
        dto.setSection(item.getSection());
        dto.setLength(item.getLength());
        dto.setPlannedQty(item.getPlannedQty());
        dto.setCompletedQty(item.getCompletedQty());
        dto.setUnit(item.getUnit());
        dto.setRemark(item.getRemark());
        return dto;
    }

    /**
     * ✅ Complete Production and Auto-create Packing Entry
     */
    @PostMapping("/{planId}/complete-and-pack")
    @Transactional
    public Map<String, Object> completeProductionAndPack(@PathVariable Long planId) {
        Map<String, Object> response = new HashMap<>();

        RollingPlanEntity plan = planRepo.findById(planId)
                .orElseThrow(() -> new RuntimeException("Rolling Plan not found"));

        // Update plan status to Completed
        plan.setStatus("Completed");
        planRepo.save(plan);

        // Auto-create packing entries for each completed item
        List<PackingEntity> packingEntries = new ArrayList<>();
        for (RollingPlanItemEntity item : plan.getItems()) {
            if (item.getPlannedQty() != null && item.getPlannedQty() > 0) {
                PackingEntity packing = new PackingEntity();
                packing.setPoNo(plan.getLoi() != null ? plan.getLoi().getLoiNumber() : "");
                packing.setGrade(item.getGrade());
                packing.setGradeSection(item.getSection());
                packing.setLength(item.getLength() != null ? item.getLength().doubleValue() : null);
                packing.setNoOfPcs(item.getPlannedQty() != null ? item.getPlannedQty().intValue() : null);
                packing.setQtyInMt(item.getPlannedQty());
                packing.setHeatNo("HEAT-" + System.currentTimeMillis());
                packing.setCustomer(plan.getLoi() != null ? plan.getLoi().getCustomerName() : "");
                packing.setPackingDate(LocalDate.now());
                packing.setStatus("PACKED");
                
                PackingEntity saved = packingRepo.save(packing);
                packingEntries.add(saved);
            }
        }

        response.put("status", "success");
        response.put("message", "Production completed and " + packingEntries.size() + " packing entries created automatically!");
        response.put("packingCount", packingEntries.size());
        return response;
    }
}
