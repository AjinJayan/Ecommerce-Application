package com.example.ecommerce.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.ecommerce.dto.JwtRequestDto;
import com.example.ecommerce.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

// @WebMvcTest(controllers = UserController.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @MockitoBean
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    // @WithMockUser(roles = "ADMIN") // not required as this api call is public
    public void shouldTestSign() throws Exception {
        JwtRequestDto jwtRequestDto = JwtRequestDto.builder().username("testuser").password("password").build();
        String token = "testtoken";
        Mockito.when(userService.signin(jwtRequestDto)).thenReturn(token);
        String json = objectMapper.writeValueAsString(jwtRequestDto);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin").contentType("application/json").content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value(token));
    }

}
