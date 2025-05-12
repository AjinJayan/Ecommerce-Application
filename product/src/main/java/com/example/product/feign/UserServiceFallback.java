package com.example.product.feign;

import org.springframework.stereotype.Component;

import com.example.product.dto.UserResponseDto;

@Component
public class UserServiceFallback implements UserService {

    @Override
    public UserResponseDto getUserById(String header, Long userId) {
        return UserResponseDto.builder().name("Service Unavalaiable").build();
    }

}
