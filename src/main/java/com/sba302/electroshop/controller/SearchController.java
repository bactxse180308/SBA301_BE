package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.GlobalSearchResponse;
import com.sba302.electroshop.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
@Slf4j
public class SearchController {

    private final SearchService searchService;

    @GetMapping
    public ApiResponse<GlobalSearchResponse> globalSearch(
            @RequestParam("q") String keyword,
            @RequestParam(value = "limit", defaultValue = "5") int limit) {
        return ApiResponse.success(searchService.globalSearch(keyword, limit));
    }
}
