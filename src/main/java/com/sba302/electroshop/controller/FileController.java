package com.sba302.electroshop.controller;

import com.sba302.electroshop.dto.response.ApiResponse;
import com.sba302.electroshop.enums.FileType;
import com.sba302.electroshop.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sba302.electroshop.dto.response.PresignedUrlResponse;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/files")
@Slf4j
public class FileController {

    private final FileStorageService fileStorageService;

    @Value("${r2.public-url:https://pub-dd1bef83e9814f348c1ea975cca22e4f.r2.dev}")
    private String publicUrlPrefix;

    @Value("${r2.bucket-name:electroshop}")
    private String bucketName;

    public FileController(@Qualifier("r2Storage") FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/upload-url")
    @PreAuthorize("isAuthenticated()")
    public ApiResponse<PresignedUrlResponse> getUploadUrl(
            @RequestParam String fileName,
            @RequestParam(defaultValue = "OTHER") FileType type) {
        String uniqueFileName = UUID.randomUUID().toString() + "_" + fileName;
        String uploadUrl = fileStorageService.getUploadPresignedUrl(uniqueFileName, type);
        
        String key = type.getFolderName() + "/" + uniqueFileName;
        String publicUrl = publicUrlPrefix+ "/" + bucketName + "/" + key;
        
        PresignedUrlResponse response = PresignedUrlResponse.builder()
                .uploadUrl(uploadUrl)
                .publicUrl(publicUrl)
                .build();
                
        return ApiResponse.success(response);
    }

    @GetMapping("/download-url")
    public ApiResponse<String> getDownloadUrl(
            @RequestParam String fileName,
            @RequestParam(defaultValue = "OTHER") FileType type) {
        String url = fileStorageService.getDownloadPresignedUrl(fileName, type);
        return ApiResponse.success(url);
    }
}
