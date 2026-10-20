package com.college.certifolio.service;

import com.college.certifolio.exception.BadRequestException;
import com.college.certifolio.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path storageLocation;
    private static final List<String> ALLOWED_MIME_TYPES = List.of(
            "application/pdf",
            "image/png",
            "image/jpeg",
            "image/jpg"
    );
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    public FileStorageService(@Value("${app.upload.dir:uploads/certificates/}") String uploadDir) {
        this.storageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.storageLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage directory: " + uploadDir, e);
        }
    }

    public String storeFile(MultipartFile file, String rollNumber) {
        if (file.isEmpty()) {
            throw new BadRequestException("Cannot upload an empty file");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BadRequestException("File exceeds maximum allowed size of 5MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Invalid file type: " + contentType + ". Only PDF, PNG, and JPEG files are allowed.");
        }

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        } else {
            extension = contentType.equals("application/pdf") ? ".pdf" : ".png";
        }

        String safeRollNo = (rollNumber != null ? rollNumber.replaceAll("[^a-zA-Z0-9]", "") : "STUDENT");
        String uniqueFilename = UUID.randomUUID() + "_" + safeRollNo + extension;

        try {
            Path targetLocation = this.storageLocation.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return uniqueFilename;
        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + uniqueFilename + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String filename) {
        try {
            Path filePath = this.storageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new ResourceNotFoundException("File not found: " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new ResourceNotFoundException("File not found: " + filename);
        }
    }

    public Path getStorageLocation() {
        return storageLocation;
    }
}
