package com.logistics.warehouse_service.service;

import com.logistics.warehouse_service.dtos.requestDTOs.StockRequest;
import com.logistics.warehouse_service.dtos.responseDTOs.StockResponse;
import com.logistics.warehouse_service.enums.TypeStock;
import com.logistics.warehouse_service.exceptions.EntrepotNotFoundException;
import com.logistics.warehouse_service.exceptions.InsufficientCapacityException;
import com.logistics.warehouse_service.exceptions.StockNotFoundException;
import com.logistics.warehouse_service.model.Entrepot;
import com.logistics.warehouse_service.model.Stock;
import com.logistics.warehouse_service.repository.EntrepotRepository;
import com.logistics.warehouse_service.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final StockRepository stockRepository;
    private final EntrepotRepository entrepotRepository;

    @Transactional
    public StockResponse createStockMovement(StockRequest request) {
        log.info("Creating stock movement for warehouse: {}", request.getEntrepotId());

        Entrepot entrepot = entrepotRepository.findById(request.getEntrepotId())
                .orElseThrow(() -> new EntrepotNotFoundException("Warehouse not found"));

        // Vérifier la capacité pour les entrées
        if (request.getType() == TypeStock.ENTREE) {
            if (!entrepot.hasCapacityFor(request.getQuantite())) {
                throw new InsufficientCapacityException(
                        String.format("Insufficient capacity. Available: %d, Required: %d",
                                entrepot.getCapaciteDisponible(), request.getQuantite())
                );
            }
        }

        // Créer le mouvement de stock
        Stock stock = new Stock();
        stock.setEntrepot(entrepot);
        stock.setColisId(request.getColisId());
        stock.setType(request.getType());
        stock.setQuantite(request.getQuantite());
        stock.setZoneStockage(request.getZoneStockage());
        stock.setReferenceDocument(request.getReferenceDocument());
        stock.setPoidsTotal(request.getPoidsTotal());
        stock.setNotes(request.getNotes());
        stock.setEffectuePar(request.getEffectuePar());

        Stock savedStock = stockRepository.save(stock);

        // Mettre à jour la capacité de l'entrepôt
        updateWarehouseCapacity(entrepot, request.getType(), request.getQuantite());

        log.info("Stock movement created with ID: {}", savedStock.getIdStock());

        return mapToStockResponse(savedStock);
    }

    @Transactional(readOnly = true)
    public StockResponse getStockById(Long id) {
        log.info("Fetching stock movement with ID: {}", id);
        Stock stock = stockRepository.findById(id)
                .orElseThrow(() -> new StockNotFoundException("Stock movement not found with ID: " + id));

        return mapToStockResponse(stock);
    }

    @Transactional(readOnly = true)
    public List<StockResponse> getAllStocks() {
        log.info("Fetching all stock movements");
        return stockRepository.findAll().stream()
                .map(this::mapToStockResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StockResponse> getStocksByEntrepot(Long entrepotId) {
        log.info("Fetching stock movements for warehouse: {}", entrepotId);
        return stockRepository.findByEntrepotIdEntrepot(entrepotId).stream()
                .map(this::mapToStockResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StockResponse> getStocksByColis(Long colisId) {
        log.info("Fetching stock movements for package: {}", colisId);
        return stockRepository.findByColisId(colisId).stream()
                .map(this::mapToStockResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StockResponse> getStocksByType(TypeStock type) {
        log.info("Fetching stock movements by type: {}", type);
        return stockRepository.findByType(type).stream()
                .map(this::mapToStockResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StockResponse> getStocksByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching stock movements between {} and {}", startDate, endDate);
        return stockRepository.findByDateMouvementBetween(startDate, endDate).stream()
                .map(this::mapToStockResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<StockResponse> getStocksByEntrepotAndDateRange(Long entrepotId,
                                                               LocalDateTime startDate,
                                                               LocalDateTime endDate) {
        log.info("Fetching stock movements for warehouse {} between {} and {}",
                entrepotId, startDate, endDate);
        return stockRepository.findByEntrepotAndDateRange(entrepotId, startDate, endDate).stream()
                .map(this::mapToStockResponse)
                .collect(Collectors.toList());
    }

    private void updateWarehouseCapacity(Entrepot entrepot, TypeStock type, Integer quantite) {
        switch (type) {
            case ENTREE:
                entrepot.setCapaciteActuelle(entrepot.getCapaciteActuelle() + quantite);
                break;
            case SORTIE:
                entrepot.setCapaciteActuelle(Math.max(0, entrepot.getCapaciteActuelle() - quantite));
                break;
            case TRANSFERT:
                // Le transfert sera géré avec deux mouvements (sortie + entrée)
                break;
            case AJUSTEMENT:
                // L'ajustement peut être positif ou négatif
                entrepot.setCapaciteActuelle(Math.max(0, entrepot.getCapaciteActuelle() + quantite));
                break;
            case RETOUR:
                entrepot.setCapaciteActuelle(entrepot.getCapaciteActuelle() + quantite);
                break;
        }
        entrepotRepository.save(entrepot);
    }

    private StockResponse mapToStockResponse(Stock stock) {
        return StockResponse.builder()
                .idStock(stock.getIdStock())
                .entrepotId(stock.getEntrepot().getIdEntrepot())
                .entrepotNom(stock.getEntrepot().getNom())
                .colisId(stock.getColisId())
                .type(stock.getType())
                .quantite(stock.getQuantite())
                .dateMouvement(stock.getDateMouvement())
                .zoneStockage(stock.getZoneStockage())
                .referenceDocument(stock.getReferenceDocument())
                .poidsTotal(stock.getPoidsTotal())
                .notes(stock.getNotes())
                .effectuePar(stock.getEffectuePar())
                .createdAt(stock.getCreatedAt())
                .build();
    }
}