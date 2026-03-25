package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.request.CreateStoreBranchRequest;
import com.sba302.electroshop.dto.response.StoreBranchResponse;
import com.sba302.electroshop.entity.BranchProductStock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface StoreBranchService {

    StoreBranchResponse getById(Integer id);

    Page<StoreBranchResponse> search(String keyword, Pageable pageable);

    StoreBranchResponse create(CreateStoreBranchRequest request);

    StoreBranchResponse update(Integer id, CreateStoreBranchRequest request);

    void delete(Integer id);

    Integer getStockQuantity(Integer branchId, Integer productId);

    void updateStock(Integer branchId, Integer productId, Integer quantity);

    /**
     * Finds the branch with the most available stock for the given product,
     * deducts the requested quantity, and increments the product's soldCount.
     *
     * @param productId the product to deduct stock for
     * @param quantity  how many units to deduct
     * @return the updated {@link BranchProductStock} record (contains branch reference)
     * @throws com.sba302.electroshop.exception.ApiException if no branch has sufficient stock
     */
    BranchProductStock findAndDeductStock(Integer productId, Integer quantity);

    BranchProductStock deductExactStock(Integer branchId, Integer productId, Integer quantity);

    /**
     * Restores stock to a specific branch and decrements the product's soldCount.
     * Used when a single item needs restoring.
     */
    void restoreStock(Integer branchId, Integer productId, Integer quantity);

    /**
     * Input record for a single stock restoration line item.
     *
     * @param branchId  the branch to restore stock to
     * @param productId the product whose stock is being restored
     * @param quantity  how many units to restore
     */
    record StockAdjustment(Integer branchId, Integer productId, Integer quantity) {}

    /**
     * Batch-restores stock for multiple items in a single transaction.
     * Fetches all relevant {@link BranchProductStock} records in one query,
     * applies adjustments in memory, and persists with a single {@code saveAll}.
     *
     * @param items list of {@link StockAdjustment} to process
     */
    void restoreStockBatch(List<StockAdjustment> items);

    /**
     * Input for branch allocation request.
     */
    record AllocationRequest(Integer productId, Integer quantity) {}

    /**
     * Result of branch allocation.
     * Maps productId to the selected BranchProductStock (which contains the branch).
     */
    record AllocationResult(Map<Integer, com.sba302.electroshop.entity.BranchProductStock> selectedStockMap) {}

    /**
     * Analyzes stock across all branches and finds the optimal (greedy) allocation
     * for the given items, prioritizing fulfillment from fewer branches.
     * Does NOT deduct stock.
     */
    AllocationResult calculateBestAllocation(List<AllocationRequest> items);
}
