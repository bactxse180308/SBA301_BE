package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.entity.Attribute;
import com.sba302.electroshop.repository.AttributeRepository;
import com.sba302.electroshop.service.AttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttributeServiceImpl implements AttributeService {

    private final AttributeRepository attributeRepository;

    @Override
    public List<Attribute> findAll() {
        return attributeRepository.findAll();
    }

    @Override
    public Optional<Attribute> findById(Integer id) {
        return attributeRepository.findById(id);
    }

    @Override
    @Transactional
    public Attribute save(Attribute attribute) {
        return attributeRepository.save(attribute);
    }

    @Override
    @Transactional
    public void deleteById(Integer id) {
        attributeRepository.deleteById(id);
    }
}
