package com.ERP.Vinar.controllers;

import com.ERP.Vinar.entities.RMStockEntity;
import com.ERP.Vinar.repositories.RMStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
@CrossOrigin(origins = "http://localhost:4200")
public class RMStockController {

    @Autowired
    private RMStockRepository stockRepo;

    @GetMapping
    public List<RMStockEntity> getAllStock() {
        return stockRepo.findAll();
    }

    @GetMapping("/location/{location}")
    public List<RMStockEntity> getStockByLocation(@PathVariable String location) {
        return stockRepo.findByLocation(location);
    }

    @GetMapping("/material/{material}")
    public List<RMStockEntity> getStockByMaterial(@PathVariable String material) {
        return stockRepo.findByMaterial(material);
    }

    @GetMapping("/location/{location}/material/{material}")
    public List<RMStockEntity> getStockByLocationAndMaterial(
            @PathVariable String location,
            @PathVariable String material
    ) {
        return stockRepo.findByLocationAndMaterial(location, material);
    }
}
