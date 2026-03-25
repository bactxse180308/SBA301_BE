package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.request.CompanyRequest;
import com.sba302.electroshop.dto.request.CreateCompanyRequest;
import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.CompanyResponse;
import com.sba302.electroshop.dto.response.CreateCompanyResponse;
import com.sba302.electroshop.enums.CompanyStatus;
import com.sba302.electroshop.service.CompanyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @GetMapping("/my-registration")
    @Operation(summary = "Get my company registration status", description = "User checks their own company's registration status")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<CompanyResponse> getMyRegistration() {
        String userIdStr = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Integer userId = Integer.parseInt(userIdStr);
        return ApiResponse.success(companyService.getByUserId(userId));
    }

    @PreAuthorize("hasRole('ADMIN') or @companySecurity.isOwner(#id)")
    @GetMapping("/{id}")
    public ApiResponse<CompanyResponse> getById(@PathVariable Integer id) {
        return ApiResponse.success(companyService.getById(id));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ApiResponse<Page<CompanyResponse>> search(
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(companyService.search(keyword, pageable));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<CompanyResponse> create(@Valid @RequestBody CompanyRequest request) {
        return ApiResponse.success(companyService.create(request));
    }

    @PreAuthorize("hasRole('ADMIN') or @companySecurity.isOwner(#id)")
    @PutMapping("/{id}")
    public ApiResponse<CompanyResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody CompanyRequest request) {
        return ApiResponse.success(companyService.update(id, request));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    @Operation(summary = "Approve/Reject company", description = "Admin updates company status (APPROVED, REJECTED, etc.)")
    public ApiResponse<CompanyResponse> updateStatus(
            @PathVariable Integer id,
            @RequestParam CompanyStatus status) {
        return ApiResponse.success(companyService.updateStatus(id, status));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        companyService.delete(id);
    }
}
