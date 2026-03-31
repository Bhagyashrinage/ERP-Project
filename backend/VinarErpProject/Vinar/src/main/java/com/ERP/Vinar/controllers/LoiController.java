package com.ERP.Vinar.controllers;

import com.ERP.Vinar.dto.LoiCreateRequest;
import com.ERP.Vinar.dto.LoiDTO;
import com.ERP.Vinar.entities.LoiEntity;
import com.ERP.Vinar.entities.LoiItemEntity;
import com.ERP.Vinar.repositories.LoiItemRepository;
import com.ERP.Vinar.repositories.LoiRepository;
import com.ERP.Vinar.services.NumberSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loi")
@CrossOrigin(origins = "http://localhost:4200")
public class LoiController {

    @Autowired
    private LoiRepository loiRepo;

    @Autowired
    private LoiItemRepository loiItemRepo;

    @Autowired
    private NumberSeriesService numberSeriesService;

    // Next LOI number (for prefilling in UI)
    @GetMapping("/next-number")
    public Map<String, String> getNextLoiNumber() {
        Map<String, String> res = new HashMap<>();
        res.put("number", numberSeriesService.nextLoiNumber());
        return res;
    }

    // Get all LOIs
    @GetMapping
    public List<LoiDTO> getAll() {
        return loiRepo.findAll().stream()
                .map(LoiDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // Get LOI by ID
    @GetMapping("/{id}")
    public LoiDTO getOne(@PathVariable Long id) {
        LoiEntity loi = loiRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("LOI not found: " + id));
        return LoiDTO.fromEntity(loi);
    }

    // Create LOI
    @PostMapping
    @Transactional
    public LoiDTO create(@RequestBody LoiCreateRequest req) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new IllegalArgumentException("LOI must contain at least one item.");
        }
        if (req.getLocation() == null || req.getLocation().isBlank()) {
            throw new IllegalArgumentException("Location is required.");
        }
        if (req.getCustomerName() == null || req.getCustomerName().isBlank()) {
            throw new IllegalArgumentException("Customer name is required.");
        }

        LoiEntity loi = new LoiEntity();
        // Always auto-generate LOI number
        String number = numberSeriesService.nextLoiNumber();
        // Extra safeguard for uniqueness
        if (loiRepo.existsByLoiNumber(number)) {
            number = numberSeriesService.nextLoiNumber();
        }

        loi.setLoiNumber(number);
        loi.setLoiDate(req.getLoiDate() != null ? req.getLoiDate() : LocalDate.now());
        loi.setCustomerName(req.getCustomerName());
        loi.setLocation(req.getLocation());
        loi.setStatus("Pending");

        List<LoiItemEntity> items = new ArrayList<>();
        for (LoiCreateRequest.Item i : req.getItems()) {
            LoiItemEntity item = new LoiItemEntity();
            item.setLoi(loi);
            item.setMaterial(i.material);
            item.setGrade(i.grade);
            item.setSection(i.section);
            item.setLength(i.length);
            item.setQuantity(i.quantity);
            item.setUnit(i.unit);
            items.add(item);
        }

        loi.setItems(items);
        LoiEntity saved = loiRepo.save(loi); // cascade saves items

        return LoiDTO.fromEntity(saved);
    }

    // Update status (Pending / Planned / Completed)
    @PutMapping("/{id}/status")
    public LoiDTO updateStatus(@PathVariable Long id, @RequestParam String status) {
        LoiEntity loi = loiRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("LOI not found: " + id));
        loi.setStatus(status);
        return LoiDTO.fromEntity(loiRepo.save(loi));
    }

    // (Optional) Add item later to an existing LOI
    @PostMapping("/{id}/items")
    @Transactional
    public LoiDTO addItem(@PathVariable Long id, @RequestBody LoiCreateRequest.Item reqItem) {
        LoiEntity loi = loiRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("LOI not found: " + id));

        LoiItemEntity item = new LoiItemEntity();
        item.setLoi(loi);
        item.setMaterial(reqItem.material);
        item.setGrade(reqItem.grade);
        item.setSection(reqItem.section);
        item.setLength(reqItem.length);
        item.setQuantity(reqItem.quantity);
        item.setUnit(reqItem.unit);

        loiItemRepo.save(item);
        return LoiDTO.fromEntity(loiRepo.findById(id).get());
    }
}
