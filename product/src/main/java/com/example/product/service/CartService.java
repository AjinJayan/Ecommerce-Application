package com.example.product.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.product.dto.CartItemRequest;
import com.example.product.model.Cart;
import com.example.product.model.CartItem;
import com.example.product.model.Product;
import com.example.product.repository.CartRepository;

@Service
public class CartService {

    @Autowired
    CartRepository cartRepository;
    @Autowired
    ProductService productService;

    public void addToCart(Long userId, CartItemRequest cartItemRequest) {
        Product product = productService.getProductById(cartItemRequest.getProductId());
        CartItem cartItem = CartItem.builder()
                .product(product)
                .quantity(cartItemRequest.getQuantity())
                .price(BigDecimal.valueOf(product.getPrice() * cartItemRequest.getQuantity()))
                .build();
        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            ArrayList<CartItem> cartItems = new ArrayList<>();
            cartItem.setCart(cart);
            cartItems.add(cartItem);
            cart.setItems(cartItems);
            cart.setTotalPrice(cartItem.getPrice());
        } else {
            cartItem.setCart(cart);
            List<CartItem> cartitems = cart.getItems();
            CartItem existing_cartItem = cartitems.stream()
                    .filter(item -> item.getProduct().getId().equals(cartItemRequest.getProductId())).findFirst()
                    .orElse(null);
            if (existing_cartItem == null) {
                cart.getItems().add(cartItem);
            } else {
                cart.getItems().remove(existing_cartItem);
                existing_cartItem.setQuantity(existing_cartItem.getQuantity() + cartItem.getQuantity());
                existing_cartItem.setPrice(existing_cartItem.getPrice().add(cartItem.getPrice()));
                cart.getItems().add(existing_cartItem);
            }

            cart.setTotalPrice(cart.getTotalPrice().add(cartItem.getPrice()));
        }
        cartRepository.save(cart);
    }

    public Cart getCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for userId: " + userId));
    }

    public void deleteCartItem(Long userId, Long cartItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for userId: " + userId));
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item not found with id: " + cartItemId));
        cart.setTotalPrice(cart.getTotalPrice().subtract(cartItem.getPrice()));
        cart.getItems().remove(cartItem);
        cartRepository.save(cart);
    }

    public void updateCartItem(Long userId, CartItemRequest cartItemRequest, Long cartItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found for userId: " + userId));
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Cart item not found with id: " + cartItemId));
        Product product = productService.getProductById(cartItemRequest.getProductId());
        cartItem.setProduct(product);
        cartItem.setQuantity(cartItemRequest.getQuantity());
        cartItem.setPrice(BigDecimal.valueOf(product.getPrice() * cartItemRequest.getQuantity()));
        cart.setTotalPrice(
                cart.getItems().stream().map(item -> item.getPrice()).reduce(BigDecimal.ZERO, BigDecimal::add));
        cartRepository.save(cart);
    }

    public void clearCart(Long id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart not found with id: " + id));
        cart.getItems().clear();
        cart.setTotalPrice(BigDecimal.ZERO);
        cartRepository.save(cart);
    }

}
