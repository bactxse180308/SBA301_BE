package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreateStoreBranchRequest;
import com.sba302.electroshop.dto.response.StoreBranchResponse;
import com.sba302.electroshop.entity.BranchProductStock;
import com.sba302.electroshop.entity.Product;
import com.sba302.electroshop.entity.StoreBranch;
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

@Service
@RequiredArgsConstructor
@Slf4j
class StoreBranchServiceImpl implements StoreBranchService {

    private final StoreBranchRepository storeBranchRepository;
    private final BranchProductStockRepository branchProductStockRepository;
    private final StoreBranchMapper storeBranchMapper;
    private final ProductRepository productRepository;

    @Override
    public StoreBranchResponse getById(Integer id) {
        // TODO: Implement - find by id, map to response
        return storeBranchRepository.findById(id)
                .map(storeBranchMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Store branch not found with id: " + id));
    }

    @Override
    public Page<StoreBranchResponse> search(String keyword, Pageable pageable) {
        // TODO: Implement - search branches by name/location
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
        // TODO: Implement - create store branch
        if (storeBranchRepository.existsByBranchNameIgnoreCase(request.getBranchName())) {
            throw new RuntimeException("Branch name already exists");
        }

        StoreBranch branch = storeBranchMapper.toEntity(request);
        StoreBranch saved = storeBranchRepository.save(branch);
        return storeBranchMapper.toResponse(saved);

    }

    @Override
    @Transactional
    public StoreBranchResponse update(Integer id, CreateStoreBranchRequest request) {

        StoreBranch branch = storeBranchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Store branch not found with id: " + id));

        storeBranchMapper.updateEntity(branch, request);
        StoreBranch updated = storeBranchRepository.save(branch);
        return storeBranchMapper.toResponse(updated);

    }


    @Override
    @Transactional
    public void delete(Integer id) {
        // TODO: Implement - delete store branch
        if (!storeBranchRepository.existsById(id)) {
            throw new RuntimeException("Store branch not found with id: " + id);
        }
        storeBranchRepository.deleteById(id);
    }

    @Override
    public Integer getStockQuantity(Integer branchId, Integer productId) {

        if (branchId == null || productId == null) {
            throw new IllegalArgumentException("Branch ID and Product ID must not be null");
        }

        BranchProductStock stock = branchProductStockRepository
                .findByBranch_BranchIdAndProduct_ProductId(branchId, productId)
                .orElseThrow(() -> new RuntimeException(
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
                        new RuntimeException("Store branch not found with id: " + branchId)
                );

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found with id: " + productId)
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


}
