package com.toystore.onlinetoystore.model;

public class Toy {
        private Long id;     // Unique toy ID
        private String name;
        private String category;  // Category (e.g., "Stuffed", "Educational")
        private Double price;
        private Integer stock; // How many in inventory
        private String description; // Toy description
        private String type;  // "ELECTRONIC" or "SOFT"
        private String imageUrl; // Image URL

        public Toy() {}
    // Constructor - creates a new Toy object
        public Toy(Long id, String name, String category, Double price,
                   Integer stock, String description, String type, String imageUrl) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.price = price;
            this.stock = stock;
            this.description = description;
            this.type = type;
            this.imageUrl = imageUrl;
        }

        // Getters
        public Long getId() { return id; }
        public String getName() { return name; }
        public String getCategory() { return category; }
        public Double getPrice() { return price; }
        public Integer getStock() { return stock; }
        public String getDescription() { return description; }
        public String getType() { return type; }
        public String getImageUrl() { return imageUrl; }

        // Setters
        public void setId(Long id) { this.id = id; }
        public void setName(String name) { this.name = name; }
        public void setCategory(String category) { this.category = category; }
        public void setPrice(Double price) { this.price = price; }
        public void setStock(Integer stock) { this.stock = stock; }
        public void setDescription(String description) { this.description = description; }
        public void setType(String type) { this.type = type; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    // Save to file: "1|Teddy Bear|Stuffed|19.99|50|Soft toy|SOFT|http://image.url"
        public String toFileString() {
            return id + "|" + name + "|" + category + "|" + price + "|" +
                    stock + "|" + description + "|" + type + "|" + (imageUrl != null ? imageUrl : "");
        }
    // Load from file

        public static Toy fromFileString(String line) {
            String[] parts = line.split("\\|");
            String imageUrl = parts.length > 7 ? parts[7] : "";
            return new Toy(
                    Long.parseLong(parts[0]),
                    parts[1],
                    parts[2],
                    Double.parseDouble(parts[3]),
                    Integer.parseInt(parts[4]),
                    parts[5],
                    parts[6],
                    imageUrl
            );
        }
}
