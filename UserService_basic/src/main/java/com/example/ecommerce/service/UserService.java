package com.example.ecommerce.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// import com.example.ecommerce.dto.JwtRequest;
import com.example.ecommerce.dto.JwtRequestDto;
import com.example.ecommerce.dto.UserDto;
import com.example.ecommerce.dto.UserResponseDto;
import com.example.ecommerce.entity.Role;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    AuthenticationManager manager;

    public String signin(JwtRequestDto jwtRequest) {
        doAutheticate(jwtRequest.getUsername(), jwtRequest.getPassword());
        UserDetails userDetails = userDetailsService.loadUserByUsername(jwtRequest.getUsername());

        return "testtoken";
    }

    public void doAutheticate(String username, String password) {
        try {
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    username, password);
            manager.authenticate(usernamePasswordAuthenticationToken);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid User Credentials");
        }
    }

    public String signup(UserDto userDto) {
        if (userRepository.existsByUsername(userDto.getUsername()))
            throw new RuntimeException("Username Already Exist");

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = User.builder().email(userDto.getEmail()).username(userDto.getUsername()).name(userDto.getName())
                .password(passwordEncoder.encode(userDto.getPassword())).build();

        if (userDto.getRole().equalsIgnoreCase("CUSTOMER")) {
            Role role = Role.builder().name("ROLE_CUSTOMER").build();
            List<Role> roles = new ArrayList<>();
            roles.add(role);
            user.setRoles(roles);
            // user.setRoles().add(role);
            userRepository.save(user);
        } else if (userDto.getRole().equalsIgnoreCase("ADMIN")) {
            Role role = Role.builder().name("ROLE_ADMIN").build();
            List<Role> roles = new ArrayList<>();
            roles.add(role);
            user.setRoles(roles);
            // user.getRoles().add(role);
            userRepository.save(user);
        } else {
            Role role = Role.builder().name("ROLE_CUSTOMER").build();
            // user.getRoles().add(role);
            List<Role> roles = new ArrayList<>();
            roles.add(role);
            user.setRoles(roles);
            userRepository.save(user);
        }

        return "User Signed Up Successfully";
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not Found with UserId: " +
                        id));
        UserResponseDto userResponseDto = UserResponseDto.builder().email(user.getEmail()).name(user.getName())
                .username(user.getUsername()).build();
        List<String> roles = new ArrayList<>();
        user.getRoles().stream().forEach(role -> roles.add(role.getName()));
        userResponseDto.setRoles(roles);
        return userResponseDto;
    }

    public List<User> getUserByKeyword(String keyword) {
        return userRepository.findByKeyword(keyword);
    }

    public String deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not Found with UserId: " + id));
        userRepository.deleteById(user.getId());
        return "User Deleted Successfully";
    }

    public String updateUser(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not Found with UserId: " + id));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        if (userDto.getEmail() != null)
            user.setEmail(userDto.getEmail());
        if (userDto.getName() != null)
            user.setName(userDto.getName());
        if (userDto.getPassword() != null)
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        if (userDto.getUsername() != null)
            user.setUsername(userDto.getUsername());

        if (userDto.getRole() != null) {
            if (userDto.getRole().equalsIgnoreCase("CUSTOMER")) {
                Role role = Role.builder().name("ROLE_CUSTOMER").build();
                user.getRoles().add(role);
            } else if (userDto.getRole().equalsIgnoreCase("ADMIN")) {
                Role role = Role.builder().name("ROLE_ADMIN").build();
                user.getRoles().add(role);
            } else {
                Role role = Role.builder().name("ROLE_CUSTOMER").build();
                user.getRoles().add(role);
            }
        }
        userRepository.save(user);
        return "User Updated SuccessFully";

    }

}
