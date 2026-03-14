package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.request.BannerCreateRequest;
import com.sba302.electroshop.dto.request.BannerStatusRequest;
import com.sba302.electroshop.dto.request.BannerUpdateRequest;
import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.BannerResponse;
import com.sba302.electroshop.enums.BannerPosition;
import com.sba302.electroshop.service.BannerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/banners")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminBannerController {

    private final BannerService bannerService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<BannerResponse>> create(@Valid @RequestBody BannerCreateRequest request) {
        BannerResponse response = bannerService.createBanner(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BannerResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody BannerUpdateRequest request) {
        BannerResponse response = bannerService.updateBanner(id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BannerResponse>> getById(@PathVariable Long id) {
        BannerResponse response = bannerService.getBannerById(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<BannerResponse>>> getAll(
            @RequestParam(required = false) BannerPosition position,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<BannerResponse> banners = bannerService.getAllBanners(position, isActive, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(banners));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<BannerResponse>> updateStatus(
            @PathVariable Long id,
            @RequestBody BannerStatusRequest request) {
        BannerResponse response = bannerService.updateBannerStatus(id, request.getIsActive());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
