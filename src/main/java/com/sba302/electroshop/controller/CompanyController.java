package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.request.CompanyRequest;
import com.sba302.electroshop.dto.request.CreateCompanyRequest;
import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.CompanyResponse;
import com.sba302.electroshop.dto.response.CreateCompanyResponse;
import com.sba302.electroshop.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
@Tag(name = "Company", description = "Company management endpoints")
public class CompanyController {

    private final CompanyService companyService;

    /**
     * Đăng ký công ty mới + tạo tài khoản User đại diện (COMPANY role).
     * Company được tạo với trạng thái PENDING, chờ admin phê duyệt.
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register company", description = "Create a new company with a linked COMPANY user account. Status is PENDING until approved by admin.")
    public ApiResponse<CreateCompanyResponse> register(@Valid @RequestBody CreateCompanyRequest request) {
        return ApiResponse.success(companyService.createWithUser(request));
    }

    @GetMapping("/{id}")
    public ApiResponse<CompanyResponse> getById(@PathVariable Integer id) {
        return ApiResponse.success(companyService.getById(id));
    }

    @GetMapping
    public ApiResponse<Page<CompanyResponse>> search(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(companyService.search(keyword, pageable));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CompanyResponse> create(@Valid @RequestBody CompanyRequest request) {
        return ApiResponse.success(companyService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<CompanyResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody CompanyRequest request) {
        return ApiResponse.success(companyService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        companyService.delete(id);
    }
}
