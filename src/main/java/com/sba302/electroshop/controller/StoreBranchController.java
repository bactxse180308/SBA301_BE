package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.entity.StoreBranch;
import com.sba302.electroshop.service.StoreBranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/store-branches")
@RequiredArgsConstructor
public class StoreBranchController {

    private final StoreBranchService storeBranchService;

    @GetMapping
    public ApiResponse<List<StoreBranch>> getAll() {
        return ApiResponse.success(storeBranchService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<StoreBranch> getById(@PathVariable Integer id) {
        return storeBranchService.findById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "StoreBranch not found"));
    }

    @PostMapping
    public ApiResponse<StoreBranch> create(@RequestBody StoreBranch storeBranch) {
        return ApiResponse.success(storeBranchService.save(storeBranch));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        storeBranchService.deleteById(id);
        return ApiResponse.success(null);
    }
}
