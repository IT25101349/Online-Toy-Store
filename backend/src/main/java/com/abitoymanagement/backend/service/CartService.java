package com.toystore.onlinetoystore.service;

import com.toystore.onlinetoystore.model.Cart;
import com.toystore.onlinetoystore.model.Toy;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
@Service
public class CartService {
    // Store all active carts in memory (userId -> Cart)
    // In production, you'd save to file, but for simplicity we use memory
    private final ConcurrentHashMap<Long, Cart> carts = new ConcurrentHashMap<>();
    private final ToyService toyService;

    public CartService(ToyService toyService) {
        this.toyService = toyService;

    }
    // Get or create cart
    public Cart getCart(Long userId) {
        return carts.computeIfAbsent(userId, Cart::new);
    }

    // Add item to cart
    public boolean addToCart(Long userId, Long toyId, int quantity) {
        Toy toy = toyService.findById(toyId);
        if (toy == null || toy.getStock() < quantity) {
            return false;
        }

        Cart cart = getCart(userId);
        cart.addItem(toy, quantity);
        return true;
    }

    // Remove item from cart
    public void removeFromCart(Long userId, Long toyId) {
        Cart cart = getCart(userId);
        cart.removeItem(toyId);
    }

    // Update item quantity
    public boolean updateQuantity(Long userId, Long toyId, int quantity) {
        Toy toy = toyService.findById(toyId);
        if (toy != null && toy.getStock() >= quantity) {
            Cart cart = getCart(userId);
            cart.updateQuantity(toyId, quantity);
            return true;
        }
        return false;
    }

    // Clear entire cart
    public void clearCart(Long userId) {
        Cart cart = getCart(userId);
        cart.clear();
    }

    // Get cart total
    public Double getCartTotal(Long userId) {
        return getCart(userId).getTotalPrice();
    }

}
