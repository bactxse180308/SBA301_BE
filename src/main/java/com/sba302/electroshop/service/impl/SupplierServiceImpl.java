package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreateSupplierRequest;
import com.sba302.electroshop.dto.response.SupplierResponse;
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
        // TODO: Implement - find by id, map to response
        return null;
    }

    @Override
    public Page<SupplierResponse> search(String keyword, Pageable pageable) {
        // TODO: Implement - search suppliers by name
        return null;
    }

    @Override
    @Transactional
    public SupplierResponse create(CreateSupplierRequest request) {
        // TODO: Implement - create supplier
        return null;
    }

    @Override
    @Transactional
    public SupplierResponse update(Integer id, CreateSupplierRequest request) {
        // TODO: Implement - update supplier
        return null;
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        // TODO: Implement - delete supplier
    }
}
