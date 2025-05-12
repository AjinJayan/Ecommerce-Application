package com.example.ecommerce.repository;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.ecommerce.entity.User;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;

    @Container
    static MySQLContainer<?> MY_SQL_CONTAINER = new MySQLContainer<>("mysql:5.7.34")
            .withDatabaseName("testdb").withUsername("test").withPassword("test");

    // @BeforeAll
    // static void startSQL() {
    // MY_SQL_CONTAINER.start();
    // }

    // @AfterAll
    // static void stopSQL() {
    // MY_SQL_CONTAINER.stop();
    // }

    @DynamicPropertySource
    static void registerDataBaseProperty(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.username", MY_SQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MY_SQL_CONTAINER::getPassword);
        registry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl);
    }

    @Test
    public void shouldTestfindByKeyword() {
        User user = User.builder().name("ajinJayan").username("ajin944").email("ajin12@gmail.com").build();
        userRepository.save(user);

        List<User> users = userRepository.findByKeyword("ajin");
        Assertions.assertThat(users.size()).isEqualTo(1);

        // Assertions.assertThat(hotel).usingRecursiveComparison().ignoringFields("id").isEqualTo(resultHotel);
        // // for compairing different objst
    }
}
