package com.sba302.electroshop.service;

import com.sba302.electroshop.dto.request.CreateCategoryRequest;
import com.sba302.electroshop.dto.response.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CategoryService {

    CategoryResponse getById(Integer id);

    Page<CategoryResponse> search(String keyword, Pageable pageable);

    CategoryResponse create(CreateCategoryRequest request);

    CategoryResponse update(Integer id, CreateCategoryRequest request);

    void delete(Integer id);
}
