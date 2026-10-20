package com.college.certifolio.controller;

import com.college.certifolio.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/files")
@Tag(name = "File Retrieval", description = "Serves uploaded certificate documents and images for in-browser preview")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @GetMapping("/{fileName:.+}")
    @Operation(summary = "View certificate proof document")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName) {
        Resource resource = fileStorageService.loadFileAsResource(fileName);

        String contentType = "application/octet-stream";
        String lowerName = fileName.toLowerCase();
        if (lowerName.endsWith(".pdf")) {
            contentType = "application/pdf";
        } else if (lowerName.endsWith(".png")) {
            contentType = "image/png";
        } else if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg")) {
            contentType = "image/jpeg";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
