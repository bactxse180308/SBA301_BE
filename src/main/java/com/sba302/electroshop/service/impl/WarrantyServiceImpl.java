package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreateWarrantyRequest;
import com.sba302.electroshop.dto.response.WarrantyResponse;
import com.sba302.electroshop.mapper.WarrantyMapper;
import com.sba302.electroshop.repository.ProductRepository;
import com.sba302.electroshop.repository.WarrantyRepository;
import com.sba302.electroshop.service.WarrantyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
class WarrantyServiceImpl implements WarrantyService {

    private final WarrantyRepository warrantyRepository;
    private final ProductRepository productRepository;
    private final WarrantyMapper warrantyMapper;

    @Override
    public WarrantyResponse getById(Integer id) {
        // TODO: Implement - find by id, map to response
        return null;
    }

    @Override
    public Page<WarrantyResponse> getByProduct(Integer productId, Pageable pageable) {
        // TODO: Implement - get warranties for product
        return null;
    }

    @Override
    @Transactional
    public WarrantyResponse create(CreateWarrantyRequest request) {
        // TODO: Implement - create warranty
        return null;
    }

    @Override
    @Transactional
    public WarrantyResponse update(Integer id, CreateWarrantyRequest request) {
        // TODO: Implement - update warranty
        return null;
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        // TODO: Implement - delete warranty
    }

    @Override
    public boolean isWarrantyValid(Integer warrantyId) {
        // TODO: Implement - check if warranty is still valid
        return false;
    }
}
