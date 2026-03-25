package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.response.StockTransactionResponse;
import com.sba302.electroshop.entity.*;
import com.sba302.electroshop.enums.TransactionType;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.mapper.StockTransactionMapper;
import com.sba302.electroshop.repository.BranchProductStockRepository;
import com.sba302.electroshop.repository.ProductRepository;
import com.sba302.electroshop.repository.StockTransactionRepository;
import com.sba302.electroshop.repository.StoreBranchRepository;
import com.sba302.electroshop.service.StockTransactionService;
import com.sba302.electroshop.specification.StockTransactionSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
class StockTransactionServiceImpl implements StockTransactionService {

    private final StockTransactionRepository stockTransactionRepository;
    private final BranchProductStockRepository branchProductStockRepository;
    private final StoreBranchRepository storeBranchRepository;
    private final ProductRepository productRepository;
    private final StockTransactionMapper stockTransactionMapper;

    // ================================================================
    // getTransactions — Xem lịch sử giao dịch
    // ================================================================
    @Override
    @Transactional(readOnly = true)
    public Page<StockTransactionResponse> getTransactions(Integer branchId, TransactionType type, Integer orderId, Integer bulkOrderId, Pageable pageable) {
        return stockTransactionRepository.findAll(
                StockTransactionSpecification.filter(branchId, type, orderId, bulkOrderId),
                pageable
        ).map(stockTransactionMapper::toResponse);
    }

    // ================================================================
    // recordReserved — ghi nhận RESERVED khi đặt hàng
    // ================================================================

    @Override
    @Transactional
    public void recordReserved(Integer orderId, List<OrderDetail> details) {
        if (details == null || details.isEmpty()) return;

        // Group details theo branch
        Map<Integer, List<OrderDetail>> byBranch = details.stream()
                .collect(Collectors.groupingBy(d -> d.getBranch().getBranchId()));

        for (Map.Entry<Integer, List<OrderDetail>> entry : byBranch.entrySet()) {
            Integer branchId = entry.getKey();
            List<OrderDetail> branchDetails = entry.getValue();

            StoreBranch branch = storeBranchRepository.findById(branchId)
                    .orElseThrow(() -> new ResourceNotFoundException("Branch not found: " + branchId));

            StockTransaction tx = StockTransaction.builder()
                    .type(TransactionType.RESERVED)
                    .branch(branch)
                    .orderId(orderId)
                    .note("Auto-reserved on order placement")
                    .createdDate(LocalDateTime.now())
                    .build();

            List<StockTransactionItem> items = branchDetails.stream()
                    .map(d -> StockTransactionItem.builder()
                            .transaction(tx)
                            .product(d.getProduct())
                            .quantity(d.getQuantity())
                            .price(d.getUnitPrice() != null ? d.getUnitPrice() : BigDecimal.ZERO)
                            .build())
                    .collect(Collectors.toList());

            tx.setItems(items);
            stockTransactionRepository.save(tx);
        }

        log.info("Recorded RESERVED stock transaction for orderId={}", orderId);
    }

    // ================================================================
    // recordExport — ghi nhận EXPORT khi warehouse xác nhận xuất
    // ================================================================

    @Override
    @Transactional
    public void recordExport(Integer orderId, Integer bulkOrderId, Integer branchId, List<ExportLine> lines) {
        if (lines == null || lines.isEmpty()) return;

        StoreBranch branch = storeBranchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found: " + branchId));

        String note = (orderId != null) 
                ? "Warehouse confirmed export for order #" + orderId
                : "Warehouse confirmed export for bulk order #" + bulkOrderId;

        StockTransaction tx = StockTransaction.builder()
                .type(TransactionType.EXPORT)
                .branch(branch)
                .orderId(orderId)
                .bulkOrderId(bulkOrderId)
                .note(note)
                .createdDate(LocalDateTime.now())
                .build();

        List<StockTransactionItem> items = lines.stream().map(line -> {
            Product product = productRepository.findById(line.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found: " + line.productId()));
            return StockTransactionItem.builder()
                    .transaction(tx)
                    .product(product)
                    .quantity(line.quantity())
                    .price(line.price() != null ? line.price() : BigDecimal.ZERO)
                    .build();
        }).collect(Collectors.toList());

        tx.setItems(items);
        stockTransactionRepository.save(tx);
        log.info("Recorded EXPORT transaction for id={}, type={}, branchId={}", 
                (orderId != null ? orderId : bulkOrderId), (orderId != null ? "ORDER" : "BULK"), branchId);
    }

    // ================================================================
    // recordCancellation — smart cancel: RELEASED or IMPORT per branch
    // ================================================================

    @Override
    @Transactional
    public void recordCancellation(Integer orderId, List<OrderDetail> details) {
        if (details == null || details.isEmpty()) return;

        // Tìm tất cả EXPORT records cho order này để biết branch nào đã xuất
        List<StockTransaction> exportedTxs = stockTransactionRepository
                .findByOrderIdAndType(orderId, TransactionType.EXPORT);

        Set<Integer> exportedBranchIds = exportedTxs.stream()
                .map(tx -> tx.getBranch().getBranchId())
                .collect(Collectors.toSet());

        // Group details theo branch
        Map<Integer, List<OrderDetail>> byBranch = details.stream()
                .collect(Collectors.groupingBy(d -> d.getBranch().getBranchId()));

        LocalDateTime now = LocalDateTime.now();

        for (Map.Entry<Integer, List<OrderDetail>> entry : byBranch.entrySet()) {
            Integer branchId = entry.getKey();
            List<OrderDetail> branchDetails = entry.getValue();

            StoreBranch branch = storeBranchRepository.findById(branchId)
                    .orElseThrow(() -> new ResourceNotFoundException("Branch not found: " + branchId));

            // Quyết định type: IMPORT nếu đã xuất, RELEASED nếu chưa
            boolean alreadyExported = exportedBranchIds.contains(branchId);
            TransactionType cancelType = alreadyExported ? TransactionType.IMPORT : TransactionType.RELEASED;
            String notePrefix = alreadyExported
                    ? "Stock returned (post-export cancel) for order #"
                    : "Reservation released (pre-export cancel) for order #";

            StockTransaction tx = StockTransaction.builder()
                    .type(cancelType)
                    .branch(branch)
                    .orderId(orderId)
                    .note(notePrefix + orderId)
                    .createdDate(now)
                    .build();

            List<StockTransactionItem> items = branchDetails.stream()
                    .map(d -> StockTransactionItem.builder()
                            .transaction(tx)
                            .product(d.getProduct())
                            .quantity(d.getQuantity())
                            .price(d.getUnitPrice() != null ? d.getUnitPrice() : BigDecimal.ZERO)
                            .build())
                    .collect(Collectors.toList());

            tx.setItems(items);
            stockTransactionRepository.save(tx);

            // Restore BranchProductStock.quantity (batch per branch)
            restoreStockForBranch(branchId, branchDetails, now);

            log.info("Recorded {} for orderId={}, branchId={} ({} items)",
                    cancelType, orderId, branchId, items.size());
        }
    }

    // ================================================================
    // Private helpers
    // ================================================================

    private void restoreStockForBranch(Integer branchId, List<OrderDetail> details, LocalDateTime now) {
        // Batch fetch product IDs
        Set<Integer> productIds = details.stream()
                .map(d -> d.getProduct().getProductId())
                .collect(Collectors.toSet());

        List<BranchProductStock> stocks = branchProductStockRepository
                .findAllByBranchIdsAndProductIds(Set.of(branchId), productIds);

        Map<Integer, BranchProductStock> stockMap = stocks.stream()
                .collect(Collectors.toMap(s -> s.getProduct().getProductId(), s -> s));

        for (OrderDetail detail : details) {
            BranchProductStock stock = stockMap.get(detail.getProduct().getProductId());
            if (stock != null) {
                stock.setQuantity(stock.getQuantity() + detail.getQuantity());
                stock.setLastUpdated(now);
            } else {
                log.warn("Stock record not found for branchId={}, productId={} during cancellation restore",
                        branchId, detail.getProduct().getProductId());
            }
        }
        branchProductStockRepository.saveAll(stocks);
    }
}
