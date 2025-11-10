package com.logistics.warehouse_service.controller;

import com.logistics.warehouse_service.dtos.requestDTOs.StockRequest;
import com.logistics.warehouse_service.dtos.responseDTOs.StockResponse;
import com.logistics.warehouse_service.enums.TypeStock;
import com.logistics.warehouse_service.service.StockService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @PostMapping
    public ResponseEntity<StockResponse> createStockMovement(@Valid @RequestBody StockRequest request) {
        StockResponse response = stockService.createStockMovement(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StockResponse> getStockById(@PathVariable Long id) {
        StockResponse response = stockService.getStockById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<StockResponse>> getAllStocks() {
        List<StockResponse> stocks = stockService.getAllStocks();
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/warehouse/{entrepotId}")
    public ResponseEntity<List<StockResponse>> getStocksByEntrepot(@PathVariable Long entrepotId) {
        List<StockResponse> stocks = stockService.getStocksByEntrepot(entrepotId);
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/package/{colisId}")
    public ResponseEntity<List<StockResponse>> getStocksByColis(@PathVariable Long colisId) {
        List<StockResponse> stocks = stockService.getStocksByColis(colisId);
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<StockResponse>> getStocksByType(@PathVariable TypeStock type) {
        List<StockResponse> stocks = stockService.getStocksByType(type);
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<StockResponse>> getStocksByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<StockResponse> stocks = stockService.getStocksByDateRange(startDate, endDate);
        return ResponseEntity.ok(stocks);
    }

    @GetMapping("/warehouse/{entrepotId}/date-range")
    public ResponseEntity<List<StockResponse>> getStocksByEntrepotAndDateRange(
            @PathVariable Long entrepotId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<StockResponse> stocks = stockService.getStocksByEntrepotAndDateRange(entrepotId, startDate, endDate);
        return ResponseEntity.ok(stocks);
    }
}