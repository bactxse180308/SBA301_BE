package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreateSupplierRequest;
import com.sba302.electroshop.dto.response.ProductResponse;
import com.sba302.electroshop.dto.response.SupplierResponse;
import com.sba302.electroshop.entity.Supplier;
import com.sba302.electroshop.exception.ResourceConflictException;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.mapper.ProductMapper;
import com.sba302.electroshop.mapper.SupplierMapper;
import com.sba302.electroshop.repository.ProductRepository;
import com.sba302.electroshop.repository.SupplierRepository;
import com.sba302.electroshop.service.SupplierService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    @Transactional(readOnly = true)
    public SupplierResponse getById(Integer id) {
        log.info("Fetching supplier with id={}", id);
        return supplierRepository.findById(id)
                .map(supplierMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SupplierResponse> search(String keyword, Pageable pageable) {
        log.info("Searching suppliers with keyword={}", keyword);
        if (keyword == null || keyword.isBlank()) {
            return supplierRepository.findAll(pageable)
                    .map(supplierMapper::toResponse);
        }

        return supplierRepository
                .findSupplierBySupplierNameContainingIgnoreCase(keyword.trim(), pageable)
                .map(supplierMapper::toResponse);
    }

    @Override
    @Transactional
    public SupplierResponse create(CreateSupplierRequest request) {
        String name = request.getSupplierName().trim();
        log.info("Checking existence of supplier name: '{}'", name);

        if (supplierRepository.existsBySupplierNameIgnoreCase(name)) {
            log.warn("Supplier name already exists: '{}'", name);
            throw new ResourceConflictException("Supplier name already exists: " + name);
        }

        Supplier supplier = supplierMapper.toEntity(request);
        supplier.setSupplierName(name);
        Supplier saved = supplierRepository.save(supplier);
        log.info("Successfully created supplier: '{}' with id={}", name, saved.getSupplierId());

        return supplierMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public SupplierResponse update(Integer id, CreateSupplierRequest request) {
        String name = request.getSupplierName().trim();
        log.info("Updating supplier id={}, new name check: '{}'", id, name);

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));

        if (supplierRepository.existsBySupplierNameIgnoreCaseAndSupplierIdNot(name, id)) {
            log.warn("Supplier name already exists (other record): '{}'", name);
            throw new ResourceConflictException("Supplier name already exists: " + name);
        }

        supplierMapper.updateEntity(supplier, request);
        supplier.setSupplierName(name);
        
        Supplier updated = supplierRepository.save(supplier);
        log.info("Successfully updated supplier id={}", id);
        return supplierMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        log.info("Deleting supplier id={}", id);
        if (!supplierRepository.existsById(id)) {
            throw new ResourceNotFoundException("Supplier not found with id: " + id);
        }
        supplierRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsBySupplierId(Integer supplierId, Pageable pageable) {
        if (!supplierRepository.existsById(supplierId)) {
            throw new ResourceNotFoundException("Supplier not found with id: " + supplierId);
        }
        return productRepository.findBySupplier_SupplierId(supplierId, pageable)
                .map(productMapper::toResponse);
    }
}
