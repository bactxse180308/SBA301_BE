package com.sba302.electroshop.service.impl;

import com.sba302.electroshop.enums.FileType;
import com.sba302.electroshop.exception.ApiException;
import com.sba302.electroshop.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.UUID;

@Service("r2Storage")
@RequiredArgsConstructor
@Slf4j
public class CloudflareR2FileStorageServiceImpl implements FileStorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${r2.bucket-name}")
    private String bucketName;

    @Override
    public String storeFile(MultipartFile file, FileType type) {
        String fileName = type.getFolderName() + "/" + UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return fileName;
        } catch (Exception e) {
            log.error("Error uploading file to R2: {}", e.getMessage());
            throw new ApiException("Failed to upload file to Cloudflare R2");
        }
    }

    @Override
    public void deleteFile(String fileName, FileType type) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            log.error("Error deleting file from R2: {}", e.getMessage());
        }
    }

    @Override
    public String getUploadPresignedUrl(String fileName, FileType type) {
        try {
            String key = type.getFolderName() + "/" + fileName;
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(15))
                    .putObjectRequest(putObjectRequest)
                    .build();

            return s3Presigner.presignPutObject(presignRequest).url().toString();
        } catch (Exception e) {
            log.error("Error generating upload presigned URL: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public String getDownloadPresignedUrl(String fileName, FileType type) {
        try {
            software.amazon.awssdk.services.s3.model.GetObjectRequest getObjectRequest = 
                software.amazon.awssdk.services.s3.model.GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName) // Key usually already contains folder prefix if stored that way
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofHours(1))
                    .getObjectRequest(getObjectRequest)
                    .build();

            return s3Presigner.presignGetObject(presignRequest).url().toString();
        } catch (Exception e) {
            log.error("Error generating download presigned URL: {}", e.getMessage());
            return null;
        }
    }
}
