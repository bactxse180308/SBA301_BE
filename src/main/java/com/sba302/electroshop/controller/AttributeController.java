package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.AttributeResponse;
import com.sba302.electroshop.service.AttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/attributes")
@RequiredArgsConstructor
public class AttributeController {

    private final AttributeService attributeService;

    @GetMapping("/{id}")
    public ApiResponse<AttributeResponse> getById(@PathVariable Integer id) {
        return ApiResponse.success(attributeService.getById(id));
    }

    @GetMapping
    public ApiResponse<Page<AttributeResponse>> search(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(attributeService.search(keyword, pageable));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<AttributeResponse> create(@RequestParam String attributeName) {
        return ApiResponse.success(attributeService.create(attributeName));
    }

    @PutMapping("/{id}")
    public ApiResponse<AttributeResponse> update(
            @PathVariable Integer id,
            @RequestParam String attributeName) {
        return ApiResponse.success(attributeService.update(id, attributeName));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        attributeService.delete(id);
        return ApiResponse.success(null);
    }
}
