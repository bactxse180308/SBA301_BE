package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreateProductRequest;
import com.sba302.electroshop.dto.request.UpdateProductRequest;
import com.sba302.electroshop.dto.response.ProductResponse;
import com.sba302.electroshop.mapper.ProductMapper;
import com.sba302.electroshop.repository.BrandRepository;
import com.sba302.electroshop.repository.CategoryRepository;
import com.sba302.electroshop.repository.ProductRepository;
import com.sba302.electroshop.repository.SupplierRepository;
import com.sba302.electroshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final SupplierRepository supplierRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse getById(Integer id) {
        // TODO: Implement - find by id, map to response
        return null;
    }

    @Override
    public Page<ProductResponse> search(String keyword, Integer categoryId, Integer brandId, Pageable pageable) {
        // TODO: Implement - search with optional filters
        return null;
    }

    @Override
    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        // TODO: Implement - map to entity, set category/brand/supplier, save
        return null;
    }

    @Override
    @Transactional
    public ProductResponse update(Integer id, UpdateProductRequest request) {
        // TODO: Implement - find, update entity, save
        return null;
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        // TODO: Implement - delete by id
    }

    @Override
    @Transactional
    public void updateStock(Integer id, Integer quantity) {
        // TODO: Implement - update product stock
    }
}
