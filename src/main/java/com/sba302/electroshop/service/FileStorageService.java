package com.sba302.electroshop.service;

import com.sba302.electroshop.enums.FileType;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file, FileType type);
    void deleteFile(String fileName, FileType type);
    String getUploadPresignedUrl(String fileName, FileType type);
    String getDownloadPresignedUrl(String fileName, FileType type);
}
