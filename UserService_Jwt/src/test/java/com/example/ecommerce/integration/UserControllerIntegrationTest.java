package com.example.ecommerce.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.ecommerce.dto.JwtRequestDto;
import com.example.ecommerce.dto.UserDto;
import com.example.ecommerce.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc

// @Testcontainers
// @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles(profiles = "test")
public class UserControllerIntegrationTest {
    @Autowired
    UserService userService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    // @Container
    // private static final MySQLContainer MY_SQL_CONTAINER = new
    // MySQLContainer<>("mysql:5.7.34")
    // .withDatabaseName("testdb").withUsername("test").withPassword("test");

    // @DynamicPropertySource
    // static void registerDataBaseProperty(DynamicPropertyRegistry registry) {
    // registry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl);
    // registry.add("spring.datasource.username", MY_SQL_CONTAINER::getUsername);
    // registry.add("spring.datasource.password", MY_SQL_CONTAINER::getPassword);

    // }

    @Test
    public void shouldTestSign() throws Exception {
        JwtRequestDto jwtRequestDto = JwtRequestDto.builder().username("testuser").password("password").build();
        String json = objectMapper.writeValueAsString(jwtRequestDto);

        UserDto userDto = UserDto.builder().email("ajin@gmail.com").name("ajin").username("testuser")
                .password("password").role("ADMIN").build();
        userService.signup(userDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/signin").contentType("application/json").content(json))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}
