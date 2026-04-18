package com.abitoymanagement.backend.model;

import java.math.BigDecimal;

public class Toy {

    private String toyId;
    private String name;
    private String category;
    private String brand;
    private BigDecimal price;
    private int quantity;
    private String ageGroup;
    private String description;
    private String imageFileName;

    public Toy() {
    }

    public Toy(String toyId, String name, String category, String brand, BigDecimal price, int quantity,
               String ageGroup, String description, String imageFileName) {
        this.toyId = toyId;
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.quantity = quantity;
        this.ageGroup = ageGroup;
        this.description = description;
        this.imageFileName = imageFileName;
    }

    public String getToyId() {
        return toyId;
    }

    public void setToyId(String toyId) {
        this.toyId = toyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getAgeGroup() {
        return ageGroup;
    }

    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }

    public String getToyType() {
        return "General Toy";
    }

    public String display() {
        return "Toy: " + name + " (" + getToyType() + "), Price: $" + price;
    }
}
