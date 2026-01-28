package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.request.CreateMediaRequest;
import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.dto.response.MediaResponse;
import com.sba302.electroshop.service.MediaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @GetMapping("/{id}")
    public ApiResponse<MediaResponse> getById(@PathVariable Integer id) {
        return ApiResponse.success(mediaService.getById(id));
    }

    @GetMapping("/product/{productId}")
    public ApiResponse<List<MediaResponse>> getByProduct(@PathVariable Integer productId) {
        return ApiResponse.success(mediaService.getByProduct(productId));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<MediaResponse> create(@Valid @RequestBody CreateMediaRequest request) {
        return ApiResponse.success(mediaService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<MediaResponse> update(
            @PathVariable Integer id,
            @Valid @RequestBody CreateMediaRequest request) {
        return ApiResponse.success(mediaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        mediaService.delete(id);
        return ApiResponse.success(null);
    }

    @PatchMapping("/{id}/sort-order")
    public ApiResponse<Void> updateSortOrder(
            @PathVariable Integer id,
            @RequestParam Integer sortOrder) {
        mediaService.updateSortOrder(id, sortOrder);
        return ApiResponse.success(null);
    }
}
