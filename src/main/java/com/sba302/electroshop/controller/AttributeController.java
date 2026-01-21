package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.entity.Attribute;
import com.sba302.electroshop.service.AttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/attributes")
@RequiredArgsConstructor
public class AttributeController {

    private final AttributeService attributeService;

    @GetMapping
    public ApiResponse<List<Attribute>> getAll() {
        return ApiResponse.success(attributeService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<Attribute> getById(@PathVariable Integer id) {
        return attributeService.findById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "Attribute not found"));
    }

    @PostMapping
    public ApiResponse<Attribute> create(@RequestBody Attribute attribute) {
        return ApiResponse.success(attributeService.save(attribute));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        attributeService.deleteById(id);
        return ApiResponse.success(null);
    }
}
