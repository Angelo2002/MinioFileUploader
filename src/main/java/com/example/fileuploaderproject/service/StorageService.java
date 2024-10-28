package com.example.fileuploaderproject.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface StorageService {

    public String uploadFile(MultipartFile file) throws IOException;
    public InputStream downloadFile(String objectName) throws IOException;

    public String generateFileHash(MultipartFile file) throws IOException;
}
