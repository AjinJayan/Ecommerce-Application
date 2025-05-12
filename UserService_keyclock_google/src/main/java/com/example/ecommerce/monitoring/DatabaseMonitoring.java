package com.example.ecommerce.monitoring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import com.example.ecommerce.repository.UserRepository;

@Component
public class DatabaseMonitoring implements HealthIndicator {
    @Autowired
    UserRepository userRepository;

    @Override
    public Health health() {
        if (isDatabaseHealthy()) {
            return Health.up().withDetail("MYSQL DATABASE", "is up and RUnning").build();
        } else
            return Health.down().withDetail("MYSQL DATABASE", "is down").build();

    }

    public boolean isDatabaseHealthy() {
        try {
            userRepository.findById(1L);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
