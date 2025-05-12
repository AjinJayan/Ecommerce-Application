package com.example.ecommerce.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

// import com.example.ecommerce.dto.JwtRequest;
import com.example.ecommerce.dto.JwtRequestDto;
import com.example.ecommerce.dto.JwtResponse;
import com.example.ecommerce.dto.UserDto;
import com.example.ecommerce.dto.UserResponseDto;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("/api/auth/signin")
    public JwtResponse signin(@RequestBody @Valid JwtRequestDto jwtRequest) {
        String token = userService.signin(jwtRequest);
        return JwtResponse.builder().token(token).build();
    }

    @PostMapping("/api/auth/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public String signup(@RequestBody UserDto userDto) {
        String message = userService.signup(userDto);
        return message;
    }

    @PutMapping("/users/{userId}")
    public String updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        String message = userService.updateUser(userId, userDto);
        return message;
    }

    @GetMapping("/api/auth/test")
    @PreAuthorize("hasRole('ADMIN')")
    public String test() {
        return "All good";
    }

    @GetMapping("/users/all")
    @PreAuthorize("hasRole('ADMIN')")
    // @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')") // if more than one role
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/users/{userId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public UserResponseDto getUserById(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @GetMapping("/users/search/{keywords}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<User> getUserByKeyword(@PathVariable String keywords) {
        return userService.getUserByKeyword(keywords);
    }

    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(@PathVariable Long userId) {
        return userService.deleteUserById(userId);
    }

}
