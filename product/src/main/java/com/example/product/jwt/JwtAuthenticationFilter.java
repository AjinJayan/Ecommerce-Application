package com.example.product.jwt;

import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.product.dto.UserResponseDto;
import com.example.product.feign.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // private RestTemplate restTemplate;
    // public JwtAuthenticationFilter(RestTemplateBuilder restTemplateBuilder) {
    // this.restTemplate = restTemplateBuilder.build();
    // }
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestHeader = request.getHeader("Authorization");
        String userId = request.getHeader("userId");
        if (requestHeader != null && requestHeader.startsWith("Bearer") && userId != null) {
            String token = requestHeader.substring(7);
            if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.setBearerAuth(token);
                HttpEntity<Void> httpEntity = new HttpEntity<>(httpHeaders);

                // resttemplate without routing
                // ResponseEntity<UserResponseDto> responseEntity = restTemplate.exchange(
                // "http://localhost:8080/users/" + userId, HttpMethod.GET, httpEntity,
                // UserResponseDto.class);
                // UserResponseDto userResponseDto = responseEntity.getBody();

                // // resttemplate with routing
                // ResponseEntity<UserResponseDto> responseEntity = restTemplate.exchange(
                // "http://USER-SERVICE/users/" + userId, HttpMethod.GET, httpEntity,
                // UserResponseDto.class);
                // UserResponseDto userResponseDto = responseEntity.getBody();
                UserResponseDto userResponseDto = userService.getUserById(requestHeader, Long.valueOf(userId));
                Collection<? extends GrantedAuthority> authorities = userResponseDto.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role))
                        .collect(Collectors.toList());
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        token, null, authorities);
                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

        filterChain.doFilter(request, response);
    }

}
