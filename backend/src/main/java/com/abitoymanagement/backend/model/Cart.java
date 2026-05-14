package com.toystore.onlinetoystore.model;
import java.util.*;

//ENCAPSULATION - Cart class hides cart items

public class Cart {
    private Long userId;                          // Which user owns this cart
    private Map<Long, CartItem> items;            // Toy ID -> CartItem

    // CartItem is an inner class (only used inside Cart)
    public static class CartItem {
        private Long toyId;
        private String toyName;
        private Double price;
        private Integer quantity;

        public CartItem(Long toyId, String toyName, Double price, Integer quantity) {
            this.toyId = toyId;
            this.toyName = toyName;
            this.price = price;
            this.quantity = quantity;
        }

        // Getters and Setters
        public Long getToyId() { return toyId; }
        public void setToyId(Long toyId) { this.toyId = toyId; }

        public String getToyName() { return toyName; }
        public void setToyName(String toyName) { this.toyName = toyName; }

        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public Double getSubtotal() {
            return price * quantity;
        }

        // For saving to file: "toyId|toyName|price|quantity"
        public String toFileString() {
            return toyId + "|" + toyName + "|" + price + "|" + quantity;
        }

        public static CartItem fromFileString(String line) {
            String[] parts = line.split("\\|");
            return new CartItem(
                    Long.parseLong(parts[0]),
                    parts[1],
                    Double.parseDouble(parts[2]),
                    Integer.parseInt(parts[3])
            );
        }
    }

    public Cart(Long userId) {
        this.userId = userId;
        this.items = new HashMap<>();
    }

    public Long getUserId() { return userId; }

    public Map<Long, CartItem> getItems() { return items; }

    // Add item to cart (or increase quantity if already exists)
    public void addItem(Toy toy, int quantity) {
        System.out.println("=== ADDING TO CART ===");
        System.out.println("Toy: " + toy.getName());
        System.out.println("Quantity: " + quantity);
        if (items.containsKey(toy.getId())) {
            // Item exists, increase quantity
            CartItem existing = items.get(toy.getId());
            existing.setQuantity(existing.getQuantity() + quantity);
        } else {
            // New item
            CartItem newItem = new CartItem(toy.getId(), toy.getName(), toy.getPrice(), quantity);
            items.put(toy.getId(), newItem);
            System.out.println("Added new item. Cart now has: " + items.size() + " items");
        }
    }

    // Remove item from cart
    public void removeItem(Long toyId) {
        items.remove(toyId);
    }

   //Update Qunatity
    public void updateQuantity(Long toyId, int quantity) {
        if (items.containsKey(toyId)) {
            if (quantity <= 0) {
                removeItem(toyId);
            } else {
                items.get(toyId).setQuantity(quantity);
            }
        }
    }

    // Get total price of all items in cart
    public Double getTotalPrice() {
        double total = 0;
        for (CartItem item : items.values()) {
            total += item.getSubtotal();
        }
        return total;
    }

    
    public void clear() {
        items.clear();
    }

   
    public boolean isEmpty() {
        return items.isEmpty();
    }

    // Get number of items in cart
    public int getItemCount() {
        return items.size();
    }
}
