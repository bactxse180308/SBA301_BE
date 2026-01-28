package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreateBrandRequest;
import com.sba302.electroshop.dto.response.BrandResponse;
import com.sba302.electroshop.mapper.BrandMapper;
import com.sba302.electroshop.repository.BrandRepository;
import com.sba302.electroshop.service.BrandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    public BrandResponse getById(Integer id) {
        // TODO: Implement - find by id, map to response
        return null;
    }

    @Override
    public Page<BrandResponse> search(String keyword, Pageable pageable) {
        // TODO: Implement - search brands by name
        return null;
    }

    @Override
    @Transactional
    public BrandResponse create(CreateBrandRequest request) {
        // TODO: Implement - create brand
        return null;
    }

    @Override
    @Transactional
    public BrandResponse update(Integer id, CreateBrandRequest request) {
        // TODO: Implement - update brand
        return null;
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        // TODO: Implement - delete brand
    }
}
