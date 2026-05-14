package com.toystore.onlinetoystore.controller;

import com.toystore.onlinetoystore.model.Order;
import com.toystore.onlinetoystore.model.User;
import com.toystore.onlinetoystore.service.OrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    //  UI PAGES 

    @GetMapping("/orders")
    public String orderHistory(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        List<Order> orders = orderService.getOrdersByUser(user.getId());
        model.addAttribute("orders", orders);
        return "orders";
    }

    @GetMapping("/orders/{orderId}")
    public String orderDetail(@PathVariable Long orderId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        Order order = orderService.getOrderById(orderId);
        if (order == null || !order.getUserId().equals(user.getId())) {
            return "redirect:/orders";
        }

        model.addAttribute("order", order);
        return "order-detail";
    }

    //  REST APIs 

    @PostMapping("/api/orders/place")
    @ResponseBody
    public Map<String, Object> placeOrder(HttpSession session,
                                          @RequestParam String shippingAddress,
                                          @RequestParam String paymentMethod) {
        Map<String, Object> response = new HashMap<>();
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            response.put("success", false);
            response.put("message", "Please login first!");
            return response;
        }

        Order order = orderService.placeOrder(user.getId(), user, shippingAddress);

        if (order != null) {
            response.put("success", true);
            response.put("message", "Order placed successfully!");
            response.put("orderId", order.getOrderId());
        } else {
            response.put("success", false);
            response.put("message", "Cart is empty or items out of stock!");
        }
        return response;
    }

    @GetMapping("/api/orders")
    @ResponseBody
    public List<Order> getUserOrders(HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return List.of();
        }
        return orderService.getOrdersByUser(user.getId());
    }

    @GetMapping("/api/orders/{orderId}")
    @ResponseBody
    public Order getOrderById(@PathVariable Long orderId, HttpSession session) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return null;
        }

        Order order = orderService.getOrderById(orderId);
        if (order != null && order.getUserId().equals(user.getId())) {
            return order;
        }
        return null;
    }

    @PostMapping("/api/orders/cancel/{orderId}")
    @ResponseBody
    public Map<String, Object> cancelOrder(@PathVariable Long orderId, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            response.put("success", false);
            response.put("message", "Please login first!");
            return response;
        }

        boolean cancelled = orderService.cancelOrder(orderId);

        response.put("success", cancelled);
        response.put("message", cancelled ? "Order cancelled successfully!" : "Cannot cancel this order (already shipped or delivered)");
        return response;
    }
}
