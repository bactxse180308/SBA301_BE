package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.StockExportRequest;
import com.sba302.electroshop.dto.request.StockImportRequest;
import com.sba302.electroshop.dto.response.StockCheckResult;
import com.sba302.electroshop.dto.response.StockItemResponse;
import com.sba302.electroshop.entity.*;
import com.sba302.electroshop.enums.TransactionType;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.mapper.WarehouseMapper;
import com.sba302.electroshop.repository.*;
import com.sba302.electroshop.service.WarehouseService;
import com.sba302.electroshop.specification.WarehouseSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseServiceImpl implements WarehouseService {

    private final BranchProductStockRepository branchProductStockRepository;
    private final StockTransactionRepository stockTransactionRepository;
    private final ProductRepository productRepository;
    private final StoreBranchRepository storeBranchRepository;
    private final WarehouseMapper warehouseMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<StockItemResponse> getInventory(String keyword, Integer branchId, Pageable pageable) {
        return branchProductStockRepository.findAll(WarehouseSpecification.filter(keyword, branchId), pageable)
                .map(warehouseMapper::toStockItemResponse);
    }

    @Override
    @Transactional
    public void importStock(StockImportRequest request) {
        StoreBranch branch = storeBranchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        StockTransaction transaction = StockTransaction.builder()
                .type(TransactionType.IMPORT)
                .branch(branch)
                .note(request.getNote())
                .createdDate(request.getCreatedDate() != null ? request.getCreatedDate() : LocalDateTime.now())
                .build();

        List<StockTransactionItem> items = request.getItems().stream().map(itemRequest -> {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemRequest.getProductId()));

            // Update Stock
            BranchProductStock stock = branchProductStockRepository.findByBranch_BranchIdAndProduct_ProductId(branch.getBranchId(), product.getProductId())
                    .orElse(BranchProductStock.builder()
                            .product(product)
                            .branch(branch)
                            .quantity(0)
                            .lastUpdated(LocalDateTime.now())
                            .build());

            stock.setQuantity(stock.getQuantity() + itemRequest.getQuantity());
            stock.setLastUpdated(LocalDateTime.now());
            branchProductStockRepository.save(stock);

            return StockTransactionItem.builder()
                    .transaction(transaction)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .price(itemRequest.getPrice())
                    .build();
        }).collect(Collectors.toList());

        transaction.setItems(items);
        stockTransactionRepository.save(transaction);
        log.info("Stock imported to branch {}: {} items", request.getBranchId(), items.size());
    }

    @Override
    @Transactional
    public void exportStock(StockExportRequest request) {
        StoreBranch branch = storeBranchRepository.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        StockTransaction transaction = StockTransaction.builder()
                .type(TransactionType.EXPORT)
                .branch(branch)
                .note(request.getNote())
                .createdDate(request.getCreatedDate() != null ? request.getCreatedDate() : LocalDateTime.now())
                .build();

        List<StockTransactionItem> items = request.getItems().stream().map(itemRequest -> {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + itemRequest.getProductId()));

            BranchProductStock stock = branchProductStockRepository.findByBranch_BranchIdAndProduct_ProductId(branch.getBranchId(), product.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Stock not found for product in this branch"));

            if (stock.getQuantity() < itemRequest.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product " + product.getProductName());
            }

            stock.setQuantity(stock.getQuantity() - itemRequest.getQuantity());
            stock.setLastUpdated(LocalDateTime.now());
            branchProductStockRepository.save(stock);

            return StockTransactionItem.builder()
                    .transaction(transaction)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .price(itemRequest.getPrice())
                    .build();
        }).collect(Collectors.toList());

        transaction.setItems(items);
        stockTransactionRepository.save(transaction);
        log.info("Stock exported from branch {}: {} items", request.getBranchId(), items.size());
    }

    @Override
    @Transactional(readOnly = true)
    public StockCheckResult checkStock(Integer branchId, Integer productId) {
        BranchProductStock stock = branchProductStockRepository.findByBranch_BranchIdAndProduct_ProductId(branchId, productId)
                .orElse(null);

        if (stock == null) {
            return StockCheckResult.builder()
                    .productId(productId)
                    .branchId(branchId)
                    .availableQuantity(0)
                    .build();
        }

        return warehouseMapper.toStockCheckResult(stock);
    }
}
