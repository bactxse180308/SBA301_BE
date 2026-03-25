package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreateStoreBranchRequest;
import com.sba302.electroshop.dto.response.StoreBranchResponse;
import com.sba302.electroshop.entity.BranchProductStock;
import com.sba302.electroshop.entity.Product;
import com.sba302.electroshop.entity.StoreBranch;
import com.sba302.electroshop.exception.ApiException;
import com.sba302.electroshop.exception.ResourceConflictException;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.mapper.StoreBranchMapper;
import com.sba302.electroshop.repository.BranchProductStockRepository;
import com.sba302.electroshop.repository.ProductRepository;
import com.sba302.electroshop.repository.StoreBranchRepository;
import com.sba302.electroshop.service.StoreBranchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
class StoreBranchServiceImpl implements StoreBranchService {

    private final StoreBranchRepository storeBranchRepository;
    private final BranchProductStockRepository branchProductStockRepository;
    private final StoreBranchMapper storeBranchMapper;
    private final ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public StoreBranchResponse getById(Integer id) {
        return storeBranchRepository.findById(id)
                .map(storeBranchMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Store branch not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<StoreBranchResponse> search(String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return storeBranchRepository.findAll(pageable)
                    .map(storeBranchMapper::toResponse);
        }

        Page<StoreBranch> branchPage =
                storeBranchRepository.findByBranchNameContainingIgnoreCaseOrLocationContainingIgnoreCase(
                        keyword, keyword, pageable);
        return branchPage.map(storeBranchMapper::toResponse);
    }

    @Override
    @Transactional
    public StoreBranchResponse create(CreateStoreBranchRequest request) {
        if (storeBranchRepository.existsByBranchNameIgnoreCase(request.getBranchName())) {
            throw new ResourceConflictException("Branch name already exists");
        }

        StoreBranch branch = storeBranchMapper.toEntity(request);
        StoreBranch saved = storeBranchRepository.save(branch);
        return storeBranchMapper.toResponse(saved);

    }

    @Override
    @Transactional
    public StoreBranchResponse update(Integer id, CreateStoreBranchRequest request) {

        StoreBranch branch = storeBranchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Store branch not found with id: " + id));

        storeBranchMapper.updateEntity(branch, request);
        StoreBranch updated = storeBranchRepository.save(branch);
        return storeBranchMapper.toResponse(updated);

    }


    @Override
    @Transactional
    public void delete(Integer id) {
        if (!storeBranchRepository.existsById(id)) {
            throw new ResourceNotFoundException("Store branch not found with id: " + id);
        }
        storeBranchRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getStockQuantity(Integer branchId, Integer productId) {

        if (branchId == null || productId == null) {
            throw new IllegalArgumentException("Branch ID and Product ID must not be null");
        }

        BranchProductStock stock = branchProductStockRepository
                .findByBranch_BranchIdAndProduct_ProductId(branchId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Stock not found for branchId=" + branchId +
                                " and productId=" + productId
                ));

        return stock.getQuantity();
    }


    @Override
    @Transactional
    public void updateStock(Integer branchId, Integer productId, Integer quantity) {

        if (branchId == null || productId == null || quantity == null) {
            throw new IllegalArgumentException("Branch ID, Product ID and Quantity must not be null");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must be >= 0");
        }

        StoreBranch branch = storeBranchRepository.findById(branchId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Store branch not found with id: " + branchId)
                );

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found with id: " + productId)
                );

        BranchProductStock stock = branchProductStockRepository
                .findByBranch_BranchIdAndProduct_ProductId(branchId, productId)
                .orElseGet(() -> {
                    BranchProductStock s = new BranchProductStock();
                    s.setBranch(branch);
                    s.setProduct(product);
                    s.setQuantity(0);
                    return s;
                });

        stock.setQuantity(quantity);
        stock.setLastUpdated(LocalDateTime.now());
        branchProductStockRepository.save(stock);
    }

    @Override
    @Transactional
    public BranchProductStock findAndDeductStock(Integer productId, Integer quantity) {
        if (productId == null || quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("productId and quantity must be non-null and quantity > 0");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        List<BranchProductStock> stocks = branchProductStockRepository.findAllByProduct_ProductId(productId);

        BranchProductStock selectedStock = stocks.stream()
                .filter(s -> s.getQuantity() != null && s.getQuantity() >= quantity)
                .max(Comparator.comparingInt(BranchProductStock::getQuantity))
                .orElseThrow(() -> new ApiException(
                        "Insufficient stock for product: " + product.getProductName()
                        + ". No branch has " + quantity + " units available."));

        // Deduct branch stock
        selectedStock.setQuantity(selectedStock.getQuantity() - quantity);
        selectedStock.setLastUpdated(LocalDateTime.now());
        branchProductStockRepository.save(selectedStock);

        // Increment soldCount
        int currentSold = product.getSoldCount() != null ? product.getSoldCount() : 0;
        product.setSoldCount(currentSold + quantity);
        productRepository.save(product);

        log.info("Deducted {} units of product {} from branch {}",
                quantity, productId, selectedStock.getBranch().getBranchId());
        return selectedStock;
    }

    @Override
    @Transactional
    public BranchProductStock deductExactStock(Integer branchId, Integer productId, Integer quantity) {
        if (branchId == null || productId == null || quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("branchId, productId and quantity must be non-null and quantity > 0");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        BranchProductStock stock = branchProductStockRepository
                .findByBranch_BranchIdAndProduct_ProductId(branchId, productId)
                .orElseThrow(() -> new ResourceNotFoundException("Stock not found for branch " + branchId + " and product " + productId));

        if (stock.getQuantity() < quantity) {
            throw new ApiException("Insufficient stock for product in selected branch.");
        }

        // Deduct branch stock
        stock.setQuantity(stock.getQuantity() - quantity);
        stock.setLastUpdated(LocalDateTime.now());
        branchProductStockRepository.save(stock);

        // Increment soldCount
        int currentSold = product.getSoldCount() != null ? product.getSoldCount() : 0;
        product.setSoldCount(currentSold + quantity);
        productRepository.save(product);

        log.info("Exactly deducted {} units of product {} from branch {}", quantity, productId, branchId);
        return stock;
    }

    @Override
    @Transactional
    public void restoreStock(Integer branchId, Integer productId, Integer quantity) {
        if (branchId == null || productId == null || quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("branchId, productId and quantity must be non-null and quantity > 0");
        }

        BranchProductStock stock = branchProductStockRepository
                .findByBranch_BranchIdAndProduct_ProductId(branchId, productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Stock record not found for branchId=" + branchId + ", productId=" + productId));

        // Restore branch stock
        stock.setQuantity(stock.getQuantity() + quantity);
        stock.setLastUpdated(LocalDateTime.now());
        branchProductStockRepository.save(stock);

        // Decrement soldCount
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));
        int currentSold = product.getSoldCount() != null ? product.getSoldCount() : 0;
        product.setSoldCount(Math.max(0, currentSold - quantity));
        productRepository.save(product);

        log.info("Restored {} units of product {} to branch {}", quantity, productId, branchId);
    }

    @Override
    @Transactional
    public void restoreStockBatch(List<StockAdjustment> items) {
        if (items == null || items.isEmpty()) return;

        // Collect unique branchIds and productIds for a single batch fetch
        Set<Integer> branchIds = new java.util.HashSet<>();
        Set<Integer> productIds = new java.util.HashSet<>();
        for (StockAdjustment adj : items) {
            branchIds.add(adj.branchId());
            productIds.add(adj.productId());
        }

        // 1 query — fetch all relevant BranchProductStock records
        List<BranchProductStock> stocks = branchProductStockRepository
                .findAllByBranchIdsAndProductIds(branchIds, productIds);

        // Build lookup: (branchId, productId) → BranchProductStock
        Map<String, BranchProductStock> stockMap = new java.util.HashMap<>();
        for (BranchProductStock s : stocks) {
            String key = s.getBranch().getBranchId() + "_" + s.getProduct().getProductId();
            stockMap.put(key, s);
        }

        // 1 query — fetch all relevant Products
        List<Product> products = productRepository.findAllById(productIds);
        Map<Integer, Product> productMap = new java.util.HashMap<>();
        for (Product p : products) {
            productMap.put(p.getProductId(), p);
        }

        // Apply adjustments in memory
        LocalDateTime now = LocalDateTime.now();
        for (StockAdjustment adj : items) {
            String key = adj.branchId() + "_" + adj.productId();
            BranchProductStock stock = stockMap.get(key);
            if (stock == null) {
                log.warn("Stock record not found for branchId={}, productId={} — skipping", adj.branchId(), adj.productId());
                continue;
            }
            stock.setQuantity(stock.getQuantity() + adj.quantity());
            stock.setLastUpdated(now);

            Product product = productMap.get(adj.productId());
            if (product != null) {
                int current = product.getSoldCount() != null ? product.getSoldCount() : 0;
                product.setSoldCount(Math.max(0, current - adj.quantity()));
            }
        }

        // 1 batch save for stocks + 1 batch save for products
        branchProductStockRepository.saveAll(stocks);
        productRepository.saveAll(products);

        log.info("Batch-restored stock for {} items across {} branches", items.size(), branchIds.size());
    }

    @Override
    @Transactional(readOnly = true)
    public AllocationResult calculateBestAllocation(List<AllocationRequest> items) {
        if (items == null || items.isEmpty()) {
            return new AllocationResult(Collections.emptyMap());
        }

        List<Integer> productIds = items.stream()
                .map(AllocationRequest::productId)
                .distinct()
                .toList();

        // 1 query for ALL available stocks for these products
        List<BranchProductStock> allStocks = branchProductStockRepository.findAllByProductIds(productIds);

        // Group stocks by productId
        Map<Integer, List<BranchProductStock>> stocksByProduct = allStocks.stream()
                .collect(Collectors.groupingBy(bps -> bps.getProduct().getProductId()));

        Map<Integer, BranchProductStock> selectedStockMap = new HashMap<>();
        List<AllocationRequest> unassigned = new ArrayList<>(items);

        while (!unassigned.isEmpty()) {
            Set<Integer> allBranchIds = allStocks.stream()
                    .map(s -> s.getBranch().getBranchId())
                    .collect(Collectors.toSet());

            int maxFulfilled = -1;
            Integer bestBranchId = null;
            List<AllocationRequest> bestFulfillableItems = new ArrayList<>();

            for (Integer branchId : allBranchIds) {
                List<AllocationRequest> fulfillable = new ArrayList<>();
                for (var item : unassigned) {
                    List<BranchProductStock> branchStocks = stocksByProduct.getOrDefault(item.productId(), Collections.emptyList());
                    boolean canFulfill = branchStocks.stream()
                            .anyMatch(s -> s.getBranch().getBranchId().equals(branchId) && s.getQuantity() >= item.quantity());
                    if (canFulfill) {
                        fulfillable.add(item);
                    }
                }

                if (fulfillable.size() > maxFulfilled) {
                    maxFulfilled = fulfillable.size();
                    bestBranchId = branchId;
                    bestFulfillableItems = fulfillable;
                }
            }

            if (maxFulfilled <= 0 || bestFulfillableItems.isEmpty()) {
                // Cannot fulfill remaining items in any single branch
                AllocationRequest firstFail = unassigned.get(0);
                throw new ApiException("Insufficient stock for product id: " + firstFail.productId()
                        + ". No single branch has " + firstFail.quantity() + " units available.");
            }

            for (var item : bestFulfillableItems) {
                final Integer chosenBranchId = bestBranchId;
                BranchProductStock selected = stocksByProduct.get(item.productId()).stream()
                        .filter(s -> s.getBranch().getBranchId().equals(chosenBranchId))
                        .findFirst()
                        .get();
                selectedStockMap.put(item.productId(), selected);
            }

            unassigned.removeAll(bestFulfillableItems);
        }

        return new AllocationResult(selectedStockMap);
    }
}
