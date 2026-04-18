package com.abitoymanagement.backend.controller;

import com.abitoymanagement.backend.dto.ApiResponse;
import com.abitoymanagement.backend.dto.ToyDTO;
import com.abitoymanagement.backend.service.FileUploadService;
import com.abitoymanagement.backend.service.ToyService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Validated
@RequestMapping("/api/toys")
public class ToyController {

    private final ToyService toyService;
    private final FileUploadService fileUploadService;

    public ToyController(ToyService toyService, FileUploadService fileUploadService) {
        this.toyService = toyService;
        this.fileUploadService = fileUploadService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ToyDTO>> addToy(@Valid @ModelAttribute ToyDTO toyDTO,
                                                      @RequestParam(value = "image", required = false) MultipartFile image) {
        ToyDTO createdToy = toyService.createToy(toyDTO, image);
        return ResponseEntity.status(201)
                .body(ApiResponse.success("Toy created successfully.", createdToy));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ToyDTO>>> getAllToys() {
        return ResponseEntity.ok(ApiResponse.success("Toys retrieved successfully.", toyService.getAllToys()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ToyDTO>> getToyById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success("Toy retrieved successfully.", toyService.getToyById(id)));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ToyDTO>> updateToy(@PathVariable String id,
                                                         @Valid @ModelAttribute ToyDTO toyDTO,
                                                         @RequestParam(value = "image", required = false) MultipartFile image) {
        ToyDTO updatedToy = toyService.updateToy(id, toyDTO, image);
        return ResponseEntity.ok(ApiResponse.success("Toy updated successfully.", updatedToy));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteToy(@PathVariable String id) {
        toyService.deleteToy(id);
        return ResponseEntity.ok(ApiResponse.success("Toy deleted successfully.", null));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ToyDTO>>> searchToys(@RequestParam String keyword) {
        return ResponseEntity.ok(ApiResponse.success("Search completed successfully.", toyService.searchToys(keyword)));
    }

    @GetMapping("/image/{filename}")
    public ResponseEntity<Resource> getToyImage(@PathVariable String filename) {
        Resource resource = fileUploadService.loadAsResource(filename);
        return ResponseEntity.ok()
                .contentType(resolveMediaType(resource))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    private MediaType resolveMediaType(Resource resource) {
        try {
            String contentType = Files.probeContentType(Paths.get(resource.getURI()));
            if (contentType != null && !contentType.isBlank()) {
                return MediaType.parseMediaType(contentType);
            }
        } catch (IOException ignored) {
        }
        return MediaType.APPLICATION_OCTET_STREAM;
    }
}
