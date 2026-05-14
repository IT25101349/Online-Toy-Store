package com.toystore.onlinetoystore.service;

import com.toystore.onlinetoystore.model.Order;
import com.toystore.onlinetoystore.model.User;
import com.toystore.onlinetoystore.model.Cart;
import com.toystore.onlinetoystore.model.Toy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
@Service
public class OrderService {
    @Value("${file.orders}")
    private String ordersFile;

    private final FileService fileService;
    private final ToyService toyService;
    private final CartService cartService;

    public OrderService(FileService fileService, ToyService toyService, CartService cartService) {
        this.fileService = fileService;
        this.toyService = toyService;
        this.cartService = cartService;
    }

    // CREATE - Place an order from cart
    public Order placeOrder(Long userId, User user, String shippingAddress) {
        Cart cart = cartService.getCart(userId);

        if (cart.isEmpty()) {
            return null;  // Cannot place empty order
        }

        // Check stock availability and reduce stock
        for (Cart.CartItem cartItem : cart.getItems().values()) {
            Toy toy = toyService.findById(cartItem.getToyId());
            if (toy == null || toy.getStock() < cartItem.getQuantity()) {
                return null;  // Out of stock
            }
        }

        // Reduce stock for each item
        for (Cart.CartItem cartItem : cart.getItems().values()) {
            Toy toy = toyService.findById(cartItem.getToyId());
            toyService.updateToy(toy.getId(), null, null, toy.getStock() - cartItem.getQuantity(), null, null);
        }

        // Create order
        long newOrderId = fileService.getNextId(ordersFile);
        Order order = new Order(newOrderId, userId, user.getFullName(),
                cart.getTotalPrice(), "PENDING", shippingAddress);

        // Add items from cart to order
        for (Cart.CartItem cartItem : cart.getItems().values()) {
            Order.OrderItem orderItem = new Order.OrderItem(
                    cartItem.getToyId(),
                    cartItem.getToyName(),
                    cartItem.getPrice(),
                    cartItem.getQuantity()
            );
            order.addItem(orderItem);
        }

        // Save order to file
        fileService.appendLine(ordersFile, order.toFileString());

        // Clear the cart
        cartService.clearCart(userId);

        return order;
    }

    // READ - Get all orders
    public List<Order> getAllOrders() {
        List<String> lines = fileService.readAllLines(ordersFile);
        return lines.stream()
                .map(Order::fromFileString)
                .filter(order -> order != null)
                .collect(Collectors.toList());
    }

    // READ - Get orders for a specific user
    public List<Order> getOrdersByUser(Long userId) {
        return getAllOrders().stream()
                .filter(order -> order.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    // READ - Get order by ID
    public Order getOrderById(Long orderId) {
        return getAllOrders().stream()
                .filter(order -> order.getOrderId().equals(orderId))
                .findFirst()
                .orElse(null);
    }

    // UPDATE - Update order status
    public boolean updateOrderStatus(Long orderId, String newStatus) {
        List<Order> orders = getAllOrders();


        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i).getOrderId().equals(orderId)) {
                orders.get(i).setStatus(newStatus);
                saveAllOrders(orders);
                System.out.println("Order " + orderId + " updated to " + newStatus);
                return true;

            }
        }
        System.out.println("Order " + orderId + " not found");
        return false;

    }

    // DELETE - Cancel order
    public boolean cancelOrder(Long orderId) {
        Order order = getOrderById(orderId);
        if (order == null || !"PENDING".equals(order.getStatus())) {
            return false;  // Can only cancel pending orders
        }

        // Restore stock
        for (Order.OrderItem item : order.getItems()) {
            Toy toy = toyService.findById(item.getToyId());
            if (toy != null) {
                toyService.updateToy(toy.getId(), null, null,
                        toy.getStock() + item.getQuantity(), null, null);
            }
        }

        // Update status to CANCELLED
        return updateOrderStatus(orderId, "CANCELLED");
    }

    private void saveAllOrders(List<Order> orders) {
        List<String> lines = orders.stream()
                .map(Order::toFileString)
                .collect(Collectors.toList());
        fileService.writeAllLines(ordersFile, lines);
    }
}
