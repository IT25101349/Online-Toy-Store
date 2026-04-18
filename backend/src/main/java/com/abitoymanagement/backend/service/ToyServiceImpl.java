package com.abitoymanagement.backend.service;

import com.abitoymanagement.backend.dto.ToyDTO;

import com.abitoymanagement.backend.exception.ToyNotFoundException;
import com.abitoymanagement.backend.model.Toy;
import com.abitoymanagement.backend.repository.ToyFileHandler;
import com.abitoymanagement.backend.util.ToyFactory;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ToyServiceImpl implements ToyService {

    private final ToyFileHandler toyFileHandler;
    private final FileUploadService fileUploadService;

    public ToyServiceImpl(ToyFileHandler toyFileHandler, FileUploadService fileUploadService) {
        this.toyFileHandler = toyFileHandler;
        this.fileUploadService = fileUploadService;
    }

    @Override
    public ToyDTO createToy(ToyDTO toyDTO, MultipartFile image) {
        List<Toy> toys = toyFileHandler.readAllToys();
        String toyId = generateNextToyId(toys);
        toyDTO.setToyId(toyId);
        String category = resolveCategory(toyDTO);
        toyDTO.setCategory(category);

        String imageFileName = fileUploadService.storeImage(image);
        try {
            Toy toy = ToyFactory.createToy(
                    toyDTO.getToyId(),
                    toyDTO.getName(),
                    category,
                    toyDTO.getBrand(),
                    toyDTO.getPrice(),
                    toyDTO.getQuantity(),
                    toyDTO.getAgeGroup(),
                    toyDTO.getDescription(),
                    imageFileName,
                    toyDTO.getToyType());

            toyFileHandler.appendToy(toy);
            return mapToDto(toy);
        } catch (RuntimeException exception) {
            fileUploadService.deleteImage(imageFileName);
            throw exception;
        }
    }

    @Override
    public List<ToyDTO> getAllToys() {
        return toyFileHandler.readAllToys().stream()
                .sorted(Comparator.comparing(Toy::getToyId))
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public ToyDTO getToyById(String toyId) {
        return mapToDto(findToyById(toyId));
    }

    @Override
    public ToyDTO updateToy(String toyId, ToyDTO toyDTO, MultipartFile image) {
        if (!toyId.equalsIgnoreCase(toyDTO.getToyId())) {
            throw new IllegalArgumentException("Path toy ID and form toyId must match.");
        }

        List<Toy> toys = toyFileHandler.readAllToys();
        Toy existingToy = toys.stream()
                .filter(toy -> toy.getToyId().equalsIgnoreCase(toyId))
                .findFirst()
                .orElseThrow(() -> new ToyNotFoundException("Toy not found with ID: " + toyId));

        String imageFileName = existingToy.getImageFileName();
        String newlyStoredImage = null;
        if (image != null && !image.isEmpty()) {
            newlyStoredImage = fileUploadService.storeImage(image);
            imageFileName = newlyStoredImage;
        }
        String category = resolveCategory(toyDTO);
        toyDTO.setCategory(category);

        try {
            Toy updatedToy = ToyFactory.createToy(
                    toyDTO.getToyId(),
                    toyDTO.getName(),
                    category,
                    toyDTO.getBrand(),
                    toyDTO.getPrice(),
                    toyDTO.getQuantity(),
                    toyDTO.getAgeGroup(),
                    toyDTO.getDescription(),
                    imageFileName,
                    toyDTO.getToyType());

            for (int index = 0; index < toys.size(); index++) {
                if (toys.get(index).getToyId().equalsIgnoreCase(toyId)) {
                    toys.set(index, updatedToy);
                    break;
                }
            }

            toyFileHandler.overwriteToys(toys);
            if (newlyStoredImage != null) {
                fileUploadService.deleteImage(existingToy.getImageFileName());
            }
            return mapToDto(updatedToy);
        } catch (RuntimeException exception) {
            if (newlyStoredImage != null) {
                fileUploadService.deleteImage(newlyStoredImage);
            }
            throw exception;
        }
    }

    @Override
    public void deleteToy(String toyId) {
        List<Toy> toys = toyFileHandler.readAllToys();
        Toy toyToDelete = toys.stream()
                .filter(toy -> toy.getToyId().equalsIgnoreCase(toyId))
                .findFirst()
                .orElseThrow(() -> new ToyNotFoundException("Toy not found with ID: " + toyId));

        List<Toy> updatedToys = toys.stream()
                .filter(toy -> !toy.getToyId().equalsIgnoreCase(toyId))
                .toList();

        toyFileHandler.overwriteToys(updatedToys);
        fileUploadService.deleteImage(toyToDelete.getImageFileName());
    }

    @Override
    public List<ToyDTO> searchToys(String keyword) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        if (normalizedKeyword.isBlank()) {
            return getAllToys();
        }

        return toyFileHandler.readAllToys().stream()
                .filter(toy -> containsIgnoreCase(toy.getName(), normalizedKeyword)
                        || containsIgnoreCase(toy.getCategory(), normalizedKeyword)
                        || containsIgnoreCase(toy.getBrand(), normalizedKeyword))
                .map(this::mapToDto)
                .toList();
    }



    private Toy findToyById(String toyId) {
        return toyFileHandler.readAllToys().stream()
                .filter(toy -> toy.getToyId().equalsIgnoreCase(toyId))
                .findFirst()
                .orElseThrow(() -> new ToyNotFoundException("Toy not found with ID: " + toyId));
    }

    private boolean containsIgnoreCase(String source, String keyword) {
        return source != null && source.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private String resolveCategory(ToyDTO toyDTO) {
        String toyType = toyDTO.getToyType();
        if (toyType != null && !toyType.isBlank()) {
            String normalizedToyType = toyType.trim().toLowerCase(Locale.ROOT);
            if (normalizedToyType.contains("electronic")) {
                return "Electronic";
            }
            if (normalizedToyType.contains("soft")) {
                return "Soft";
            }
            return "General";
        }

        String category = toyDTO.getCategory();
        if (category == null || category.isBlank()) {
            return "General";
        }
        return category.trim();
    }

    private String generateNextToyId(List<Toy> toys) {
        int maxId = toys.stream()
                .map(Toy::getToyId)
                .filter(id -> id != null && id.startsWith("TSR"))
                .map(id -> id.substring(3))
                .filter(s -> s.matches("\\d+"))
                .mapToInt(Integer::parseInt)
                .max()
                .orElse(0);

        return String.format("TSR%03d", maxId + 1);
    }

    private ToyDTO mapToDto(Toy toy) {
        ToyDTO toyDTO = new ToyDTO();
        toyDTO.setToyId(toy.getToyId());
        toyDTO.setName(toy.getName());
        toyDTO.setCategory(toy.getCategory());
        toyDTO.setBrand(toy.getBrand());
        toyDTO.setPrice(toy.getPrice());
        toyDTO.setQuantity(toy.getQuantity());
        toyDTO.setAgeGroup(toy.getAgeGroup());
        toyDTO.setDescription(toy.getDescription());
        toyDTO.setImageFileName(toy.getImageFileName());
        toyDTO.setImageUrl(buildImageUrl(toy.getImageFileName()));
        toyDTO.setToyType(toy.getToyType());
        toyDTO.setDisplayMessage(toy.display());
        return toyDTO;
    }

    private String buildImageUrl(String imageFileName) {
        if (imageFileName == null || imageFileName.isBlank()) {
            return null;
        }
        if (imageFileName.startsWith("http://") || imageFileName.startsWith("https://")) {
            return imageFileName;
        }
        return "/api/toys/image/" + imageFileName;
    }
}
