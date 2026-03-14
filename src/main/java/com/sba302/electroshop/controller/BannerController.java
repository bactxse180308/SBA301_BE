package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.HomeBannerResponse;
import com.sba302.electroshop.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/banners")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;

    @GetMapping("/home")
    public ResponseEntity<ApiResponse<HomeBannerResponse>> getHomeBanners() {
        HomeBannerResponse banners = bannerService.getHomeBanners();
        return ResponseEntity.ok(ApiResponse.success(banners));
    }
}
