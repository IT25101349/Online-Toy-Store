package com.toystore.onlinetoystore.service;

import com.toystore.onlinetoystore.model.Toy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ToyService {
    @Value("${file.toys}")
    private String toysFile;

    private final FileService fileService;

    public ToyService(FileService fileService) {
        this.fileService = fileService;
    }

    // CREATE - Add a new toy
    public Toy addToy(String name, String category, Double price,
                      Integer stock, String description, String type, String imageUrl) {
        long newId = fileService.getNextId(toysFile);
        Toy toy = new Toy(newId, name, category, price, stock, description, type, imageUrl);
        fileService.appendLine(toysFile, toy.toFileString());
        return toy;
    }

    // READ - Get all toys
    public List<Toy> getAllToys() {
        List<String> lines = fileService.readAllLines(toysFile);
        return lines.stream()
                .map(Toy::fromFileString)
                .collect(Collectors.toList());
    }

    // READ - Find toy by ID
    public Toy findById(Long id) {
        return getAllToys().stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // READ - Search toys by name (case-insensitive)
    public List<Toy> searchByName(String keyword) {
        return getAllToys().stream()
                .filter(t -> t.getName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }

    // READ - Filter by category
    public List<Toy> filterByCategory(String category) {
        return getAllToys().stream()
                .filter(t -> t.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    // UPDATE - Modify a toy
    public boolean updateToy(Long id, String name, Double price, Integer stock, String description, String imageUrl) {
        List<Toy> toys = getAllToys();
        boolean found = false;

        for (int i = 0; i < toys.size(); i++) {
            if (toys.get(i).getId().equals(id)) {
                Toy t = toys.get(i);
                if (name != null && !name.isEmpty()) t.setName(name);
                if (price != null) t.setPrice(price);
                if (stock != null) t.setStock(stock);
                if (description != null && !description.isEmpty()) t.setDescription(description);
                if (imageUrl != null) t.setImageUrl(imageUrl);
                toys.set(i, t);
                found = true;
                break;
            }
        }

        if (found) {
            saveAllToys(toys);
        }
        return found;
    }

    // DELETE - Remove a toy
    public boolean deleteToy(Long id) {
        List<Toy> toys = getAllToys();
        boolean removed = toys.removeIf(t -> t.getId().equals(id));

        if (removed) {
            saveAllToys(toys);
        }
        return removed;
    }

    // Private helper - saves all toys to file
    private void saveAllToys(List<Toy> toys) {
        List<String> lines = toys.stream()
                .map(Toy::toFileString)
                .collect(Collectors.toList());
        fileService.writeAllLines(toysFile, lines);
    }
}
