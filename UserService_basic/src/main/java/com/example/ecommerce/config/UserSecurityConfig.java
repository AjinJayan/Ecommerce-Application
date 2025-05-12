package com.example.ecommerce.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class UserSecurityConfig {

    @Autowired
    UserDetailsService userDetailsService;

    // httpBAsic
    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // http
    // .csrf(csrf -> csrf.disable())
    // .authorizeHttpRequests(authorize -> authorize
    // .requestMatchers("/api/auth/signin", "/api/auth/signup").permitAll()
    // .anyRequest()
    // .authenticated())
    // .httpBasic(Customizer.withDefaults());

    // return http.build();

    // }

    // formLogin
    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    // http
    // .csrf(csrf -> csrf.disable())
    // .authorizeHttpRequests(authorize -> authorize
    // .requestMatchers("/api/auth/signin", "/api/auth/signup").permitAll()
    // .anyRequest()
    // .authenticated())
    // .formLogin(Customizer.withDefaults());

    // return http.build();

    // }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/signin", "/api/auth/signup").permitAll()
                        .anyRequest()
                        .authenticated())
                .formLogin(form -> form.loginPage("/login").permitAll())
                .rememberMe(rememberme -> rememberme.userDetailsService(userDetailsService))
                .logout(logout -> logout.deleteCookies("remember-me"));

        return http.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // if we are using in memory datbase we don't need this
    @Bean
    public AuthenticationManager manager(AuthenticationConfiguration builder)
            throws Exception {
        return builder.getAuthenticationManager();
    }

    // @Bean
    // UserDetailsService users() {
    // UserDetails user1 =
    // User.builder().username("ajin").password(passwordEncoder().encode("password"))
    // .roles("ADMIN").build();
    // UserDetails user2 =
    // User.builder().username("jayan").password(passwordEncoder().encode("nopassword"))
    // .roles("CUSTOMER").build();
    // return new InMemoryUserDetailsManager(user1, user2);
    // }

}
