package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.request.CreateStoreBranchRequest;
import com.sba302.electroshop.dto.response.StoreBranchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoreBranchService {

    StoreBranchResponse getById(Integer id);

    Page<StoreBranchResponse> search(String keyword, Pageable pageable);

    StoreBranchResponse create(CreateStoreBranchRequest request);

    StoreBranchResponse update(Integer id, CreateStoreBranchRequest request);

    void delete(Integer id);

    Integer getStockQuantity(Integer branchId, Integer productId);

    void updateStock(Integer branchId, Integer productId, Integer quantity);
}
