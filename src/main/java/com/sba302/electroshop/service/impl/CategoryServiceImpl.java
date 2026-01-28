package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreateCategoryRequest;
import com.sba302.electroshop.dto.response.CategoryResponse;
import com.sba302.electroshop.mapper.CategoryMapper;
import com.sba302.electroshop.repository.CategoryRepository;
import com.sba302.electroshop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse getById(Integer id) {
        // TODO: Implement - find by id, map to response
        return null;
    }

    @Override
    public Page<CategoryResponse> search(String keyword, Pageable pageable) {
        // TODO: Implement - search categories by name
        return null;
    }

    @Override
    @Transactional
    public CategoryResponse create(CreateCategoryRequest request) {
        // TODO: Implement - create category
        return null;
    }

    @Override
    @Transactional
    public CategoryResponse update(Integer id, CreateCategoryRequest request) {
        // TODO: Implement - update category
        return null;
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        // TODO: Implement - delete category
    }
}
