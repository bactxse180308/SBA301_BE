package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.request.CreateBrandRequest;
import com.sba302.electroshop.dto.response.BrandResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BrandService {

    BrandResponse getById(Integer id);

    Page<BrandResponse> search(String keyword, Pageable pageable);

    BrandResponse create(CreateBrandRequest request);

    BrandResponse update(Integer id, CreateBrandRequest request);

    void delete(Integer id);
}
