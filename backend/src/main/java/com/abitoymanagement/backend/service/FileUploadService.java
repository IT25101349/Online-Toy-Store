package com.abitoymanagement.backend.service;

import com.abitoymanagement.backend.config.FileStorageProperties;
import com.abitoymanagement.backend.exception.InvalidImageException;
import com.abitoymanagement.backend.exception.ResourceNotFoundException;
import com.abitoymanagement.backend.exception.StorageException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png");
    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of("image/jpeg", "image/png");

    private final Path uploadDirectory;

    public FileUploadService(FileStorageProperties properties) {
        this.uploadDirectory = Path.of(properties.getUploadDir()).normalize().toAbsolutePath();
        initializeUploadDirectory();
    }

    public String storeImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        validateImage(file);

        String extension = extractExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID() + extension;

        try {
            Path targetPath = resolveUploadPath(fileName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException exception) {
            throw new StorageException("Failed to store the uploaded image.", exception);
        }
    }

    public Resource loadAsResource(String fileName) {
        try {
            Path filePath = resolveUploadPath(fileName);
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new ResourceNotFoundException("Image file not found: " + fileName);
            }
            return resource;
        } catch (MalformedURLException exception) {
            throw new StorageException("Failed to load the image file.", exception);
        }
    }

    public void deleteImage(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            return;
        }

        try {
            Files.deleteIfExists(resolveUploadPath(fileName));
        } catch (IOException exception) {
            throw new StorageException("Failed to delete image file: " + fileName, exception);
        }
    }

    private void validateImage(MultipartFile file) {
        String extension = extractExtension(file.getOriginalFilename());
        String contentType = file.getContentType();

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new InvalidImageException("Only jpg, jpeg, and png image files are allowed.");
        }

        if (contentType != null
                && !contentType.isBlank()
                && !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase(Locale.ROOT))) {
            throw new InvalidImageException("Invalid image content type. Only JPEG and PNG files are allowed.");
        }
    }

    private String extractExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new InvalidImageException("Uploaded file must have a valid image extension.");
        }
        return fileName.substring(fileName.lastIndexOf('.')).toLowerCase(Locale.ROOT);
    }

    private Path resolveUploadPath(String fileName) {
        if (fileName == null || fileName.isBlank()) {
            throw new InvalidImageException("Image file name must not be empty.");
        }

        Path resolvedPath = uploadDirectory.resolve(fileName).normalize();
        if (!resolvedPath.startsWith(uploadDirectory)) {
            throw new InvalidImageException("Invalid image file path.");
        }
        return resolvedPath;
    }

    private void initializeUploadDirectory() {
        try {
            Files.createDirectories(uploadDirectory);
        } catch (IOException exception) {
            throw new StorageException("Failed to initialize upload directory.", exception);
        }
    }
}
