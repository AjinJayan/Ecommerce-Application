package com.example.ecommerce.service;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.ecommerce.dto.JwtRequestDto;
import com.example.ecommerce.dto.UserDto;
import com.example.ecommerce.entity.Role;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.jwt.JwtAutheticationHelper;
import com.example.ecommerce.repository.UserRepository;

@SpringBootTest
public class UserServiceTest {
        @MockitoBean
        AuthenticationManager manager;

        @MockitoBean
        UserDetailsService userDetailsService;

        @MockitoBean
        JwtAutheticationHelper helper;

        @MockitoBean
        UserRepository userRepository;

        @InjectMocks
        UserService userService;

        @BeforeEach
        void setup() {
                MockitoAnnotations.openMocks(this);
        }

        @Test
        @DisplayName("Testing Succesful Login")
        void shouldTestSuccessfulLogin() {
                JwtRequestDto jwtRequestDto = JwtRequestDto.builder().username("ajin").password("password").build();
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                jwtRequestDto.getUsername(), jwtRequestDto.getPassword());
                Mockito.when(manager.authenticate(usernamePasswordAuthenticationToken)).thenReturn(null);

                UserDetails userDetails = User.builder().username(jwtRequestDto.getUsername())
                                .password(jwtRequestDto.getPassword()).build();
                List<Role> roles = new ArrayList<>();
                Role role = Role.builder().name("ADMIN").build();
                roles.add(role);
                ((User) userDetails).setRoles(roles);
                Mockito.when(userDetailsService.loadUserByUsername(jwtRequestDto.getUsername()))
                                .thenReturn(userDetails);

                String testToken = "token";
                Mockito.when(helper.generateToken(userDetails)).thenReturn(testToken);

                String resultToken = userService.signin(jwtRequestDto);

                // Assertions.assertEquals(resultToken, testToken);
                Assertions.assertThat(resultToken).isEqualTo(testToken);
        }

        @Test
        @DisplayName("Testing Failed Login")
        void shouldTestFailedLogin() {
                JwtRequestDto jwtRequestDto = JwtRequestDto.builder().username("ajin").password("password").build();
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                jwtRequestDto.getUsername(), jwtRequestDto.getPassword());
                Mockito.when(manager.authenticate(usernamePasswordAuthenticationToken))
                                .thenThrow(new BadCredentialsException("Invalid Credentials"));

                // UserDetails userDetails =
                // User.builder().username(jwtRequestDto.getUsername())
                // .password(jwtRequestDto.getPassword()).roles("ADMIN").build();
                // Mockito.when(userDetailsService.loadUserByUsername(jwtRequestDto.getUsername())).thenReturn(userDetails);

                // String testToken = "token";
                // Mockito.when(helper.generateToken(userDetails)).thenReturn(testToken);

                // String resultToken = userService.signin(jwtRequestDto);

                // Assertions.assertEquals(resultToken, testToken);
                Assertions.assertThatThrownBy(() -> userService.signin(jwtRequestDto))
                                .isInstanceOf(BadCredentialsException.class);
        }

        @Test
        void shouldTestSaveNewUser() {
                UserDto userDto = UserDto.builder().email("ajin@gamil.com").name("ajin").password("password")
                                .role("ADMIN").username("ajin944").build();
                Mockito.when(userRepository.existsByUsername(userDto.getUsername())).thenReturn(false);
                Mockito.when(userRepository.save(Mockito.any(User.class))).thenReturn(null);
                String resultString = userService.signup(userDto);

                Assertions.assertThat(resultString).isEqualTo("User Signed Up Successfully");
                // verfyig userReositoy method is called atleast once with User class as inputs
                Mockito.verify(userRepository,
                                Mockito.times(1)).save(Mockito.any(User.class));
        }
}
