package com.toystore.onlinetoystore.controller;

import com.toystore.onlinetoystore.model.Cart;
import com.toystore.onlinetoystore.model.User;
import com.toystore.onlinetoystore.model.Toy;
import com.toystore.onlinetoystore.service.CartService;
import com.toystore.onlinetoystore.service.ToyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller  // 
public class CartController {

    private final CartService cartService;
    private final ToyService toyService;  // 

    public CartController(CartService cartService, ToyService toyService) {
        this.cartService = cartService;
        this.toyService = toyService;
    }

    // UI PAGE 

    @GetMapping("/cart")
    public String viewCart(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        Cart cart = cartService.getCart(user.getId());
        model.addAttribute("cart", cart);
        model.addAttribute("total", cart.getTotalPrice());
        return "cart";
    }

    //  APIs 

    @PostMapping("/api/cart/add")
    @ResponseBody
    public Map<String, Object> addToCart(HttpSession session,
                                         @RequestParam Long toyId,
                                         @RequestParam(defaultValue = "1") int quantity) {
        Map<String, Object> response = new HashMap<>();
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            response.put("success", false);
            response.put("message", "Please login first!");
            return response;
        }

        Toy toy = toyService.findById(toyId);
        if (toy == null) {
            response.put("success", false);
            response.put("message", "Toy not found!");
            return response;
        }

        boolean added = cartService.addToCart(user.getId(), toyId, quantity);

        if (added) {
            response.put("success", true);
            response.put("message", "Added to cart!");
        } else {
            response.put("success", false);
            response.put("message", "Not enough stock!");
        }
        return response;
    }

    @DeleteMapping("/api/cart/remove/{toyId}")
    @ResponseBody
    public Map<String, Object> removeFromCart(HttpSession session, @PathVariable Long toyId) {
        Map<String, Object> response = new HashMap<>();
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            response.put("success", false);
            response.put("message", "Please login!");
            return response;
        }

        cartService.removeFromCart(user.getId(), toyId);
        response.put("success", true);
        response.put("message", "Removed from cart!");
        return response;
    }

    @PutMapping("/api/cart/update")
    @ResponseBody
    public Map<String, Object> updateQuantity(HttpSession session,
                                              @RequestParam Long toyId,
                                              @RequestParam int quantity) {
        Map<String, Object> response = new HashMap<>();
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            response.put("success", false);
            response.put("message", "Please login!");
            return response;
        }

        boolean updated = cartService.updateQuantity(user.getId(), toyId, quantity);
        response.put("success", updated);
        response.put("message", updated ? "Quantity updated!" : "Update failed!");
        return response;
    }

    @GetMapping("/api/cart")
    @ResponseBody
    public Map<String, Object> getCart(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            response.put("success", false);
            response.put("message", "Not logged in");
            return response;
        }

        Cart cart = cartService.getCart(user.getId());
        response.put("success", true);
        response.put("items", cart.getItems());
        response.put("total", cart.getTotalPrice());
        response.put("itemCount", cart.getItemCount());

        return response;
    }
    @GetMapping("/checkout")
    public String showCheckout(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        Cart cart = cartService.getCart(user.getId());
        if (cart.isEmpty()) {
            return "redirect:/cart";
        }

        model.addAttribute("user", user);
        model.addAttribute("cart", cart);
        model.addAttribute("total", cart.getTotalPrice());
        return "checkout";
    }
}