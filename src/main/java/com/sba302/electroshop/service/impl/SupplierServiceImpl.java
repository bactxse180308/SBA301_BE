package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreateSupplierRequest;
import com.sba302.electroshop.dto.response.SupplierResponse;
import com.sba302.electroshop.entity.Supplier;
import com.sba302.electroshop.mapper.SupplierMapper;
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
class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;

    @Override
    public SupplierResponse getById(Integer id) {

        return supplierRepository.findById(id)
                .map(supplierMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));
    }

    @Override
    public Page<SupplierResponse> search(String keyword, Pageable pageable) {

        if (keyword == null || keyword.isBlank()) {
            return supplierRepository.findAll(pageable)
                    .map(supplierMapper::toResponse);
        }

        return supplierRepository
                .findSupplierBySupplierNameContainingIgnoreCase(keyword, pageable)
                .map(supplierMapper::toResponse);
    }


    @Override
    @Transactional
    public SupplierResponse create(CreateSupplierRequest request) {

        Supplier supplier = supplierMapper.toEntity(request);

        Supplier saved = supplierRepository.save(supplier);
        if (supplierRepository.existsBySupplierNameIgnoreCase(request.getSupplierName())) {
            throw new RuntimeException("Supplier name already exists");
        }


        return supplierMapper.toResponse(saved);
    }



    @Override
    @Transactional
    public SupplierResponse update(Integer id, CreateSupplierRequest request) {

        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));

        supplierMapper.updateEntity(supplier, request);

        Supplier updated = supplierRepository.save(supplier);
        return supplierMapper.toResponse(updated);
    }



    @Override
    @Transactional
    public void delete(Integer id) {
        if (!supplierRepository.existsById(id)) {
            throw new RuntimeException("Supplier not found with id: " + id);
        }
        supplierRepository.deleteById(id);

    }
}
