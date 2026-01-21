package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.entity.Review;
import com.sba302.electroshop.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public ApiResponse<List<Review>> getAll() {
        return ApiResponse.success(reviewService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<Review> getById(@PathVariable Integer id) {
        return reviewService.findById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "Review not found"));
    }

    @PostMapping
    public ApiResponse<Review> create(@RequestBody Review review) {
        return ApiResponse.success(reviewService.save(review));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        reviewService.deleteById(id);
        return ApiResponse.success(null);
    }
}
