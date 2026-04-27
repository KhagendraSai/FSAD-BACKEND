package com.fsad.backend.service;

import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {

    private final Path uploadRoot;

    public FileStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.uploadRoot = Path.of(uploadDir).toAbsolutePath().normalize();
    }

    public String store(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is required");
        }

        try {
            Path targetDir = uploadRoot.resolve(folder).normalize();
            Files.createDirectories(targetDir);

            String originalName = file.getOriginalFilename() == null ? "upload.bin" : file.getOriginalFilename();
            String sanitizedName = originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
            String storedName = UUID.randomUUID() + "-" + sanitizedName;
            Path target = targetDir.resolve(storedName);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            }

            return target.toString();
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to store uploaded file", ex);
        }
    }

    public Resource asResource(String storedPath) {
        if (storedPath == null || storedPath.isBlank()) {
            throw new EntityNotFoundException("File path is empty");
        }

        if (storedPath.startsWith("http://") || storedPath.startsWith("https://")) {
            try {
                return new UrlResource(storedPath);
            } catch (MalformedURLException ex) {
                throw new IllegalStateException("Invalid file path", ex);
            }
        }

        Path path = Path.of(storedPath).toAbsolutePath().normalize();
        if (!Files.exists(path)) {
            throw new EntityNotFoundException("File not found");
        }

        try {
            return new UrlResource(path.toUri());
        } catch (MalformedURLException ex) {
            throw new IllegalStateException("Invalid file path", ex);
        }
    }

    public String detectContentType(String storedPath, String fallback) {
        if (storedPath != null) {
            String normalized = storedPath.toLowerCase();
            if (normalized.endsWith(".pdf")) return "application/pdf";
            if (normalized.endsWith(".png")) return "image/png";
            if (normalized.endsWith(".jpg") || normalized.endsWith(".jpeg")) return "image/jpeg";
            if (normalized.endsWith(".gif")) return "image/gif";
            if (normalized.endsWith(".webp")) return "image/webp";
            if (normalized.endsWith(".txt")) return "text/plain";
        }

        try {
            String detected = Files.probeContentType(Path.of(storedPath));
            if (detected != null && !detected.isBlank()) {
                return detected;
            }
        } catch (IOException ignored) {
            // Use fallback when probing fails.
        }

        if (fallback != null && !fallback.isBlank()) {
            return fallback;
        }
        return "application/octet-stream";
    }
}
