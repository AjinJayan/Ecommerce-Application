package com.example.product.service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.product.dto.OrderRequest;
import com.example.product.model.Cart;
import com.example.product.model.Order;
import com.example.product.model.OrderItem;
import com.example.product.repository.OrderRepository;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    CartService cartService;

    public void placeOrder(OrderRequest orderRequest) {
        Cart cart = cartService.getCart(orderRequest.getUserId());
        Order order = new Order();
        order.setUserId(orderRequest.getUserId());
        order.setTotalPrice(cart.getTotalPrice());
        order.setAddress(orderRequest.getAddress());
        order.setPayment(orderRequest.getPayment());
        order.setCreatedDate(new Date().toString());
        order.setOrderItems(cart.getItems().stream()
                .map(item -> OrderItem.builder()
                        .product(item.getProduct())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .order(order)
                        .build())
                .collect(Collectors.toList()));

        cartService.clearCart(cart.getId());
        orderRepository.save(order);
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
