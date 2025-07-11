package com.ops.hunting.knowledge.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class StorageService {

    private static final Logger logger = LoggerFactory.getLogger(StorageService.class);

    @Value("${storage.type:local}")
    private String storageType;

    @Value("${storage.local.path:./uploads}")
    private String localStoragePath;

    @Value("${storage.s3.bucket:persistent-hunt-artifacts}")
    private String s3BucketName;

    private final S3Client s3Client;

    public StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String storeFile(MultipartFile file, String investigationId) throws IOException {
        String fileName = generateFileName(file.getOriginalFilename());
        String filePath = buildFilePath(investigationId, fileName);

        if ("s3".equalsIgnoreCase(storageType)) {
            return storeFileInS3(file, filePath);
        } else {
            return storeFileLocally(file, filePath);
        }
    }

    public byte[] retrieveFile(String filePath) {
        try {
            if ("s3".equalsIgnoreCase(storageType)) {
                return retrieveFileFromS3(filePath);
            } else {
                return retrieveFileLocally(filePath);
            }
        } catch (Exception e) {
            logger.error("Failed to retrieve file: {}", filePath, e);
            throw new RuntimeException("Failed to retrieve file: " + e.getMessage());
        }
    }

    private String storeFileInS3(MultipartFile file, String filePath) throws IOException {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3BucketName)
                    .key(filePath)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            logger.debug("Stored file in S3: {}", filePath);
            return filePath;
        } catch (Exception e) {
            logger.error("Failed to store file in S3: {}", filePath, e);
            throw new IOException("Failed to store file in S3: " + e.getMessage());
        }
    }

    private String storeFileLocally(MultipartFile file, String filePath) throws IOException {
        Path fullPath = Paths.get(localStoragePath, filePath);
        
        // Create directories if they don't exist
        Files.createDirectories(fullPath.getParent());
        
        // Store the file
        Files.write(fullPath, file.getBytes());
        logger.debug("Stored file locally: {}", fullPath);
        return filePath;
    }

    private byte[] retrieveFileFromS3(String filePath) throws IOException {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(s3BucketName)
                    .key(filePath)
                    .build();

            return s3Client.getObjectAsBytes(getObjectRequest).asByteArray();
        } catch (Exception e) {
            logger.error("Failed to retrieve file from S3: {}", filePath, e);
            throw new IOException("Failed to retrieve file from S3: " + e.getMessage());
        }
    }

    private byte[] retrieveFileLocally(String filePath) throws IOException {
        Path fullPath = Paths.get(localStoragePath, filePath);
        if (!Files.exists(fullPath)) {
            throw new IOException("File not found: " + filePath);
        }
        return Files.readAllBytes(fullPath);
    }

    private String generateFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID().toString() + extension;
    }

    private String buildFilePath(String investigationId, String fileName) {
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return String.format("%s/%s/%s", datePath, investigationId != null ? investigationId : "general", fileName);
    }
}
