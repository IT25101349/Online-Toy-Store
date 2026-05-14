package com.toystore.onlinetoystore.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Order {
    private Long orderId;
    private Long userId;
    private String userName;
    private List<OrderItem> items;
    private Double totalAmount;
    private String status;      // "PENDING", "SHIPPED", "DELIVERED", "CANCELLED"
    private String orderDate;
    private String shippingAddress;

    public static class OrderItem {
        private Long toyId;
        private String toyName;
        private Double price;
        private Integer quantity;
        private Double subtotal;

        public OrderItem(Long toyId, String toyName, Double price, Integer quantity) {
            this.toyId = toyId;
            this.toyName = toyName;
            this.price = price;
            this.quantity = quantity;
            this.subtotal = price * quantity;
        }

        // Getters
        public Long getToyId() {
            return toyId;
        }

        public String getToyName() {
            return toyName;
        }

        public Double getPrice() {
            return price;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public Double getSubtotal() {
            return subtotal;
        }

        // For file storage: "toyId|toyName|price|quantity|subtotal"
        public String toFileString() {
            return toyId + "|" + toyName + "|" + price + "|" + quantity + "|" + subtotal;
        }

        public static OrderItem fromFileString(String line) {
            if (line == null || line.trim().isEmpty()) {
                return null;
            }
            String[] parts = line.split("\\|");

            if (parts.length < 5) {
                System.err.println("Invalid order item line (skipping): " + line);
                return null;
            }
            OrderItem item = new OrderItem(
                    Long.parseLong(parts[0]),
                    parts[1],
                    Double.parseDouble(parts[2]),
                    Integer.parseInt(parts[3])
            );
            item.subtotal = Double.parseDouble(parts[4]);
            return item;
        }
    }

    public Order() {
        this.items = new ArrayList<>();
    }

    public Order(Long orderId, Long userId, String userName, Double totalAmount,
                 String status, String shippingAddress) {
        this.orderId = orderId;
        this.userId = userId;
        this.userName = userName;
        this.items = new ArrayList<>();
        this.totalAmount = totalAmount;
        this.status = status;
        this.shippingAddress = shippingAddress;
        this.orderDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    // Getters and Setters
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    // Add an item to the order
    public void addItem(OrderItem item) {
        items.add(item);
    }

    // Calculate total from items
    public void calculateTotal() {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getSubtotal();
        }
        this.totalAmount = total;
    }

    // Save order to file format
    public String toFileString() {
        StringBuilder sb = new StringBuilder();
        sb.append(orderId).append("|");
        sb.append(userId).append("|");
        sb.append(userName).append("|");
        sb.append(totalAmount).append("|");
        sb.append(status).append("|");
        sb.append(orderDate).append("|");
        sb.append(shippingAddress).append("|");

        // Save items as JSON-like format: [item1|item2|item3]
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(items.get(i).toFileString());
        }

        return sb.toString();
    }

    // Load order from file
    public static Order fromFileString(String line) {
        // Skip empty lines
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        String[] parts = line.split("\\|");

        // Check if we have enough parts
        if (parts.length < 7) {
            System.err.println("Invalid order line (not enough parts): " + line);
            return null;
        }

        Order order = new Order();
        order.setOrderId(Long.parseLong(parts[0]));
        order.setUserId(Long.parseLong(parts[1]));
        order.setUserName(parts[2]);
        order.setTotalAmount(Double.parseDouble(parts[3]));
        order.setStatus(parts[4]);
        order.setOrderDate(parts[5]);
        order.setShippingAddress(parts[6]);

        if (parts.length > 7 && parts[7] != null && !parts[7].isEmpty()) {
            String itemsStr = parts[7];
            String[] itemParts = itemsStr.split(",");
            for (String itemStr : itemParts) {
                OrderItem item = OrderItem.fromFileString(itemStr);
                if (item != null) {
                    order.addItem(item);
                }

                return order;
            }
        }
        return order;
    }
}