package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.entity.Media;
import com.sba302.electroshop.service.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/media")
@RequiredArgsConstructor
public class MediaController {

    private final MediaService mediaService;

    @GetMapping
    public ApiResponse<List<Media>> getAll() {
        return ApiResponse.success(mediaService.findAll());
    }

    @GetMapping("/{id}")
    public ApiResponse<Media> getById(@PathVariable Integer id) {
        return mediaService.findById(id)
                .map(ApiResponse::success)
                .orElse(ApiResponse.error(404, "Media not found"));
    }

    @PostMapping
    public ApiResponse<Media> create(@RequestBody Media media) {
        return ApiResponse.success(mediaService.save(media));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Integer id) {
        mediaService.deleteById(id);
        return ApiResponse.success(null);
    }
}
