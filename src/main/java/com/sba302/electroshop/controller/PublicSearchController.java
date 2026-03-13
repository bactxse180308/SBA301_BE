package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.PublicSearchResponse;
import com.sba302.electroshop.service.PublicSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicSearchController {

    private final PublicSearchService publicSearchService;

    @GetMapping("/search")
    public ApiResponse<PublicSearchResponse> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "5") int limit) {
        return ApiResponse.success(publicSearchService.search(q, limit));
    }
}
