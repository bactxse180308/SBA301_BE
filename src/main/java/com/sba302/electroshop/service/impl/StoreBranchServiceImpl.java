package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreateStoreBranchRequest;
import com.sba302.electroshop.dto.response.StoreBranchResponse;
import com.sba302.electroshop.mapper.StoreBranchMapper;
import com.sba302.electroshop.repository.BranchProductStockRepository;
import com.sba302.electroshop.repository.StoreBranchRepository;
import com.sba302.electroshop.service.StoreBranchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
class StoreBranchServiceImpl implements StoreBranchService {

    private final StoreBranchRepository storeBranchRepository;
    private final BranchProductStockRepository branchProductStockRepository;
    private final StoreBranchMapper storeBranchMapper;

    @Override
    public StoreBranchResponse getById(Integer id) {
        // TODO: Implement - find by id, map to response
        return null;
    }

    @Override
    public Page<StoreBranchResponse> search(String keyword, Pageable pageable) {
        // TODO: Implement - search branches by name/location
        return null;
    }

    @Override
    @Transactional
    public StoreBranchResponse create(CreateStoreBranchRequest request) {
        // TODO: Implement - create store branch
        return null;
    }

    @Override
    @Transactional
    public StoreBranchResponse update(Integer id, CreateStoreBranchRequest request) {
        // TODO: Implement - update store branch
        return null;
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        // TODO: Implement - delete store branch
    }

    @Override
    public Integer getStockQuantity(Integer branchId, Integer productId) {
        // TODO: Implement - get product stock at branch
        return null;
    }

    @Override
    @Transactional
    public void updateStock(Integer branchId, Integer productId, Integer quantity) {
        // TODO: Implement - update product stock at branch
    }
}
