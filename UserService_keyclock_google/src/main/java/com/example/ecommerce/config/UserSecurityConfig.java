package com.example.ecommerce.config;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.example.ecommerce.entity.Role;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class UserSecurityConfig {

    private final static String ISSUR_URI = "accounts.google.com";

    private UserRepository userRepository;

    public UserSecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/actuator").permitAll()
                        .anyRequest()
                        .authenticated())
                .oauth2Login(oauth -> oauth.loginPage("/login").defaultSuccessUrl("/api/auth/test"))
                .oauth2ResourceServer(
                        oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));

        return http.build();

    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Set<GrantedAuthority> mappeGrantedAuthorities = new HashSet<>();
            Map<String, Object> claims = jwt.getClaims();

            if (claims.containsKey("realm_access")) {
                Map<String, Object> realmAccessMap = (Map<String, Object>) claims.get("realm_access");
                if (realmAccessMap.containsKey("roles")) {
                    List<String> roles = (List<String>) realmAccessMap.get("roles");

                    mappeGrantedAuthorities.addAll(
                            roles.stream().map(role -> new SimpleGrantedAuthority(role))
                                    .collect(Collectors.toSet()));
                }

            }
            return mappeGrantedAuthorities;
        });
        return converter;
    }

    @Bean
    GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> {
            Set<GrantedAuthority> mappeGrantedAuthorities = new HashSet<>();
            authorities.forEach(authority -> {
                if (OAuth2UserAuthority.class.isInstance(authority)) {
                    OAuth2UserAuthority oAuth2UserAuthority = (OAuth2UserAuthority) authority;
                    Map<String, Object> userAttributeMap = oAuth2UserAuthority.getAttributes();

                    if (userAttributeMap.containsKey("realm_access")) {
                        Map<String, Object> realmAccessMap = (Map<String, Object>) userAttributeMap.get("realm_access");

                        if (realmAccessMap.containsKey("roles")) {
                            List<String> roles = (List<String>) realmAccessMap.get("roles");
                            mappeGrantedAuthorities.addAll(
                                    roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                                            .collect(Collectors.toSet()));
                        }
                    }
                }

                if (OidcUserAuthority.class.isInstance(authority)) {
                    OidcUserAuthority oidcUserAuthority = (OidcUserAuthority) authority;
                    Map<String, Object> userAuserAuthoritiesMapper = oidcUserAuthority.getAttributes();

                    if (oidcUserAuthority.getIdToken().getIssuer().toString().contains(ISSUR_URI)) {
                        String email = (String) userAuserAuthoritiesMapper.get("email");
                        User user = userRepository.findByEmail(email);

                        if (user != null) {
                            List<Role> roles = user.getRoles();
                            mappeGrantedAuthorities.addAll(roles.stream()
                                    .map(role -> new SimpleGrantedAuthority(role.getName().toUpperCase()))
                                    .collect(Collectors.toList()));
                        }
                    }
                }
            });

            return mappeGrantedAuthorities;
        };
    }

    @Bean
    JwtDecoder jwtDecoder() {
        return JwtDecoders.fromIssuerLocation("https://lemur-6.cloud-iam.com/auth/realms/productapp");
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

}
