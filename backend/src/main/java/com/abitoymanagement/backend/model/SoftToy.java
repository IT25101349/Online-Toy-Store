package com.abitoymanagement.backend.model;

import java.math.BigDecimal;

public class SoftToy extends Toy {

    public SoftToy(String toyId, String name, String category, String brand, BigDecimal price, int quantity,
                   String ageGroup, String description, String imageFileName) {
        super(toyId, name, category, brand, price, quantity, ageGroup, description, imageFileName);
    }

    @Override
    public String getToyType() {
        return "Soft Toy";
    }

    @Override
    public String display() {
        return "[Soft] " + getName() + " - Soft and safe for kids.";
    }
}
