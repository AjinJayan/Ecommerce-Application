package com.example.product.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import com.example.product.dto.UserResponseDto;

// here behind the scene, the resilence4j circuit breaker is working, if this servive fails, fallback mechanism is executed
// this fallback wwon't get triggered 4XX faliure
@FeignClient(name = "USER-SERVICE", fallback = UserServiceFallback.class)
public interface UserService {
    @GetMapping("/users/{userId}")
    // @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public UserResponseDto getUserById(@RequestHeader("Authorization") String header, @PathVariable Long userId);
}
