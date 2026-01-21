package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.entity.Category;
import com.sba302.electroshop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<Category>> getAll() {
        return ApiResponse.success(categoryService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<Category> getById(@PathVariable Integer id) {
        return categoryService.findById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "Category not found"));
    }

    @PostMapping
    public ApiResponse<Category> create(@RequestBody Category category) {
        return ApiResponse.success(categoryService.save(category));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        categoryService.deleteById(id);
        return ApiResponse.success(null);
    }
}
