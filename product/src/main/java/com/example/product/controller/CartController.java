package com.example.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.product.dto.CartItemRequest;
import com.example.product.model.Cart;
import com.example.product.service.CartService;

@RestController
@RequestMapping("/cart")
public class CartController {
    @Autowired
    CartService cartService;

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Void> addToCart(@RequestHeader("userId") Long userId,
            @RequestBody CartItemRequest cartItemRequest) {
        cartService.addToCart(userId, cartItemRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @GetMapping("/get")
    @PreAuthorize("hasAnyRole('ADMIN','CUSTOMER')")
    public ResponseEntity<Cart> getCart(@RequestHeader("userId") Long userId) {
        Cart cart = cartService.getCart(userId);
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping("/delete/{cartItemId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Void> deleteCartItem(@RequestHeader("userId") Long userId,
            @PathVariable Long cartItemId) {
        cartService.deleteCartItem(userId, cartItemId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update/{cartItemId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<Void> updateCartItem(@RequestHeader("userId") Long userId,
            @RequestBody CartItemRequest cartItemRequest, @PathVariable Long cartItemId) {
        cartService.updateCartItem(userId, cartItemRequest, cartItemId);
        return ResponseEntity.ok().build();
    }
}
