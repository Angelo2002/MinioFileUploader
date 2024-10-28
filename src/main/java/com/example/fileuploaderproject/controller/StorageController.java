package com.example.fileuploaderproject.controller;

import com.example.fileuploaderproject.service.StorageService;
import com.example.fileuploaderproject.service.StorageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLOutput;

@RequiredArgsConstructor
@RequestMapping("/api/files")
@RestController
public class StorageController {
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB


    private final StorageService minioService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.getSize() > MAX_FILE_SIZE) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("File size exceeds the maximum limit of 10MB");
        }

        try {
            String fileHash = minioService.generateFileHash(file);
            System.out.println("File hash: " + fileHash);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        try {
            String objectName = minioService.uploadFile(file);
            return ResponseEntity.ok("File uploaded successfully: " + objectName);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("File upload failed: " + e.getMessage());
        }
    }

    @GetMapping("/download/{filename}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String filename) {
        try {
            InputStream inputStream = minioService.downloadFile(filename);
            InputStreamResource resource = new InputStreamResource(inputStream);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }




}
