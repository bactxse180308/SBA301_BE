package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.entity.Company;
import com.sba302.electroshop.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping
    public ApiResponse<List<Company>> getAll() {
        return ApiResponse.success(companyService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<Company> getById(@PathVariable Integer id) {
        return companyService.findById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "Company not found"));
    }

    @PostMapping
    public ApiResponse<Company> create(@RequestBody Company company) {
        return ApiResponse.success(companyService.save(company));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        companyService.deleteById(id);
        return ApiResponse.success(null);
    }
}
