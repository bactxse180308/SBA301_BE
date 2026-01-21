package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.entity.Brand;
import com.sba302.electroshop.repository.BrandRepository;
import com.sba302.electroshop.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    @Override
    public List<Brand> findAll() {
        return brandRepository.findAll();
    }

    @Override
    public Optional<Brand> findById(Integer id) {
        return brandRepository.findById(id);
    }

    @Override
    @Transactional
    public Brand save(Brand brand) {
        return brandRepository.save(brand);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        brandRepository.deleteById(id);
    }
}
