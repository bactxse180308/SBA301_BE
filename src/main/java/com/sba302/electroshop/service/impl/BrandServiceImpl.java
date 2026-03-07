package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreateBrandRequest;
import com.sba302.electroshop.dto.response.BrandResponse;
import com.sba302.electroshop.entity.Brand;
import com.sba302.electroshop.exception.ResourceNotFoundException;
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
@Transactional(readOnly = true)
class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;
    private final BrandMapper brandMapper;

    @Override
    public BrandResponse getById(Integer id) {
        log.info("Fetching brand with id={}", id);
        return brandRepository.findById(id)
                .map(brandMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));
    }

    @Override
    public Page<BrandResponse> getAll(Pageable pageable) {
        log.info("Fetching all brands");
        return brandRepository.findAll(pageable)
                .map(brandMapper::toResponse);
    }

    @Override
    public Page<BrandResponse> search(String keyword, Pageable pageable) {
        log.info("Searching brands with keyword={}", keyword);
        return brandRepository.findByBrandNameContainingIgnoreCase(keyword, pageable)
                .map(brandMapper::toResponse);
    }

    @Override
    @Transactional
    public BrandResponse create(CreateBrandRequest request) {
        log.info("Creating brand: {}", request.getBrandName());
        Brand brand = brandMapper.toEntity(request);
        brand = brandRepository.save(brand);
        return brandMapper.toResponse(brand);
    }

    @Override
    @Transactional
    public BrandResponse update(Integer id, CreateBrandRequest request) {
        log.info("Updating brand id={}", id);
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found with id: " + id));
        brandMapper.updateEntity(brand, request);
        brand = brandRepository.save(brand);
        return brandMapper.toResponse(brand);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        log.info("Deleting brand id={}", id);
        if (!brandRepository.existsById(id)) {
            throw new ResourceNotFoundException("Brand not found with id: " + id);
        }
        brandRepository.deleteById(id);
    }
}
