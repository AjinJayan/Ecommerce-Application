package com.example.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.product.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
