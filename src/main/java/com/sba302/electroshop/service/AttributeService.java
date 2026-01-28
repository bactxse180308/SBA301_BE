package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.response.AttributeResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AttributeService {

    AttributeResponse getById(Integer id);

    Page<AttributeResponse> search(String keyword, Pageable pageable);

    AttributeResponse create(String attributeName);

    AttributeResponse update(Integer id, String attributeName);

    void delete(Integer id);
}
