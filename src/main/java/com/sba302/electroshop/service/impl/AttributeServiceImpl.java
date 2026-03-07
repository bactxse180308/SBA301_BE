package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.response.AttributeResponse;
import com.sba302.electroshop.entity.Attribute;
import com.sba302.electroshop.exception.ResourceNotFoundException;
import com.sba302.electroshop.mapper.AttributeMapper;
import com.sba302.electroshop.repository.AttributeRepository;
import com.sba302.electroshop.service.AttributeService;
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
class AttributeServiceImpl implements AttributeService {

    private final AttributeRepository attributeRepository;
    private final AttributeMapper attributeMapper;

    @Override
    public AttributeResponse getById(Integer id) {
        log.info("Fetching attribute with id={}", id);
        return attributeRepository.findById(id)
                .map(attributeMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Attribute not found with id: " + id));
    }

    @Override
    public Page<AttributeResponse> getAll(Pageable pageable) {
        log.info("Fetching all attributes");
        return attributeRepository.findAll(pageable)
                .map(attributeMapper::toResponse);
    }

    @Override
    public Page<AttributeResponse> search(String keyword, Pageable pageable) {
        log.info("Searching attributes with keyword={}", keyword);
        return attributeRepository.findByAttributeNameContainingIgnoreCase(keyword, pageable)
                .map(attributeMapper::toResponse);
    }

    @Override
    @Transactional
    public AttributeResponse create(String attributeName) {
        log.info("Creating attribute: {}", attributeName);
        Attribute attribute = Attribute.builder()
                .attributeName(attributeName)
                .build();
        attribute = attributeRepository.save(attribute);
        return attributeMapper.toResponse(attribute);
    }

    @Override
    @Transactional
    public AttributeResponse update(Integer id, String attributeName) {
        log.info("Updating attribute id={}", id);
        Attribute attribute = attributeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attribute not found with id: " + id));
        attribute.setAttributeName(attributeName);
        attribute = attributeRepository.save(attribute);
        return attributeMapper.toResponse(attribute);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        log.info("Deleting attribute id={}", id);
        if (!attributeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attribute not found with id: " + id);
        }
        attributeRepository.deleteById(id);
    }
}
