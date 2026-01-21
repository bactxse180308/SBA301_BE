package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.entity.Warranty;
import com.sba302.electroshop.repository.WarrantyRepository;
import com.sba302.electroshop.service.WarrantyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WarrantyServiceImpl implements WarrantyService {

    private final WarrantyRepository warrantyRepository;

    @Override
    public List<Warranty> findAll() {
        return warrantyRepository.findAll();
    }

    @Override
    public Optional<Warranty> findById(Integer id) {
        return warrantyRepository.findById(id);
    }

    @Override
    @Transactional
    public Warranty save(Warranty warranty) {
        return warrantyRepository.save(warranty);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        warrantyRepository.deleteById(id);
    }
}
