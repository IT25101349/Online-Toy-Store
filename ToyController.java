package com.toystore.onlinetoystore.controller;

import com.toystore.onlinetoystore.model.Toy;
import com.toystore.onlinetoystore.service.ToyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ToyController {

    private final ToyService toyService;

    public ToyController(ToyService toyService) {
        this.toyService = toyService;
    }

    // UI Page - Show all toys
    @GetMapping("/toys")
    public String showToys(Model model) {
        model.addAttribute("toys", toyService.getAllToys());
        return "toys";
    }

    // ============================================
    // TOY APIs
    // ============================================

    // CREATE - Add a new toy
    @PostMapping("/api/toys")
    @ResponseBody
    public Map<String, Object> addToy(@RequestParam String name,
                                      @RequestParam String category,
                                      @RequestParam Double price,
                                      @RequestParam Integer stock,
                                      @RequestParam String description,
                                      @RequestParam String type,
                                      @RequestParam(required = false) String imageUrl) {
        Map<String, Object> response = new HashMap<>();
        Toy toy = toyService.addToy(name, category, price, stock, description, type, imageUrl);

        response.put("success", true);
        response.put("message", "Toy added successfully!");
        response.put("toy", toy);
        return response;
    }

    // READ - Get all toys
    @GetMapping("/api/toys")
    @ResponseBody
    public List<Toy> getAllToys() {
        return toyService.getAllToys();
    }

    // READ - Get one toy by ID
    @GetMapping("/api/toys/{id}")
    @ResponseBody
    public Toy getToyById(@PathVariable Long id) {
        return toyService.findById(id);
    }

    // READ - Search toys by keyword
    @GetMapping("/api/toys/search")
    @ResponseBody
    public List<Toy> searchToys(@RequestParam String keyword) {
        return toyService.searchByName(keyword);
    }

    // UPDATE - Modify a toy
    @PutMapping("/api/toys/{id}")
    @ResponseBody
    public Map<String, Object> updateToy(@PathVariable Long id,
                                         @RequestParam(required = false) String name,
                                         @RequestParam(required = false) Double price,
                                         @RequestParam(required = false) Integer stock,
                                         @RequestParam(required = false) String description,
                                         @RequestParam(required = false) String imageUrl) {
        Map<String, Object> response = new HashMap<>();
        boolean updated = toyService.updateToy(id, name, price, stock, description, imageUrl);

        response.put("success", updated);
        response.put("message", updated ? "Toy updated successfully!" : "Toy not found!");
        return response;
    }

    // DELETE - Remove a toy
    @DeleteMapping("/api/toys/{id}")
    @ResponseBody
    public Map<String, Object> deleteToy(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        boolean deleted = toyService.deleteToy(id);

        response.put("success", deleted);
        response.put("message", deleted ? "Toy deleted successfully!" : "Toy not found!");
        return response;
    }
}
