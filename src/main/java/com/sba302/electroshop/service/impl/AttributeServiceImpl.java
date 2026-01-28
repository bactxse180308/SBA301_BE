package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.response.AttributeResponse;
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
class AttributeServiceImpl implements AttributeService {

    private final AttributeRepository attributeRepository;
    private final AttributeMapper attributeMapper;

    @Override
    public AttributeResponse getById(Integer id) {
        // TODO: Implement - find by id, map to response
        return null;
    }

    @Override
    public Page<AttributeResponse> search(String keyword, Pageable pageable) {
        // TODO: Implement - search attributes by name
        return null;
    }

    @Override
    @Transactional
    public AttributeResponse create(String attributeName) {
        // TODO: Implement - create attribute
        return null;
    }

    @Override
    @Transactional
    public AttributeResponse update(Integer id, String attributeName) {
        // TODO: Implement - update attribute
        return null;
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        // TODO: Implement - delete attribute
    }
}
