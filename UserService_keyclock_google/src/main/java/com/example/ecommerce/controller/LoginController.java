package com.example.ecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/userDetails")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('admin')")
    // Don't make it simply a sTring, if we do like that, it will try to find the
    // tymeleaf template with the same string.
    public ResponseEntity<String> getUserDetails(@AuthenticationPrincipal OidcUser oidcUser) {
        return ResponseEntity.ok().body(
                "Username: %s, Email %s".formatted(oidcUser.getFullName().toString(), oidcUser.getEmail().toString()));
    }

}
