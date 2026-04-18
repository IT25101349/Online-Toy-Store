package com.abitoymanagement.backend.model;

import java.math.BigDecimal;

public class ElectronicToy extends Toy {

    public ElectronicToy(String toyId, String name, String category, String brand, BigDecimal price, int quantity,
                         String ageGroup, String description, String imageFileName) {
        super(toyId, name, category, brand, price, quantity, ageGroup, description, imageFileName);
    }

    @Override
    public String getToyType() {
        return "Electronic Toy";
    }

    @Override
    public String display() {
        return "[Electronic] " + getName() + " (" + getBrand() + ") - Handled with electronic safety.";
    }
}
