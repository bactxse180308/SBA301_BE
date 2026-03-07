package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.dto.request.CreateCategoryRequest;
import com.sba302.electroshop.dto.response.CategoryResponse;
import com.sba302.electroshop.entity.Category;
import com.sba302.electroshop.exception.ResourceNotFoundException;
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
@Transactional(readOnly = true)
class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryResponse getById(Integer id) {
        log.info("Fetching category with id={}", id);
        return categoryRepository.findById(id)
                .map(categoryMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    @Override
    public Page<CategoryResponse> getAll(Pageable pageable) {
        log.info("Fetching all categories");
        return categoryRepository.findAll(pageable)
                .map(categoryMapper::toResponse);
    }

    @Override
    public Page<CategoryResponse> search(String keyword, Pageable pageable) {
        log.info("Searching categories with keyword={}", keyword);
        return categoryRepository.findByCategoryNameContainingIgnoreCase(keyword, pageable)
                .map(categoryMapper::toResponse);
    }

    @Override
    @Transactional
    public CategoryResponse create(CreateCategoryRequest request) {
        log.info("Creating category: {}", request.getCategoryName());
        Category category = categoryMapper.toEntity(request);
        category = categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public CategoryResponse update(Integer id, CreateCategoryRequest request) {
        log.info("Updating category id={}", id);
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        categoryMapper.updateEntity(category, request);
        category = categoryRepository.save(category);
        return categoryMapper.toResponse(category);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        log.info("Deleting category id={}", id);
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
