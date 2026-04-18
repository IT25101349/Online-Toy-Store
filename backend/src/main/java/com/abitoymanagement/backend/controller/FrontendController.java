package com.abitoymanagement.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendController {

    @GetMapping("/")
    public String redirectToFrontend() {
        return "forward:/index.html";
    }

    @GetMapping({"/inventory", "/inventory/"})
    public String inventory() {
        return "forward:/pages/toys.html";
    }

    @GetMapping({"/inventory/add", "/inventory/add/"})
    public String addInventoryItem() {
        return "forward:/pages/add-toy.html";
    }

    @GetMapping({"/inventory/details", "/inventory/details/"})
    public String inventoryDetails() {
        return "forward:/pages/toy-details.html";
    }

    @GetMapping({"/inventory/edit", "/inventory/edit/"})
    public String editInventoryItem() {
        return "forward:/pages/edit-toy.html";
    }
}
