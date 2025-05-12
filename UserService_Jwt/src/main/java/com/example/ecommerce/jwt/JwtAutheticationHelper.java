package com.example.ecommerce.jwt;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtAutheticationHelper {

    private final static String SECRET_KEY = "asdkhjbasfybewqbofakheiabwebawefbkbwqejbkawebuhqwebkjqbwafdgsfgsfdgsdfgdasfgsdfgdfgsdfgsfdgsfdgsafgdsfvs4245634635463546dsfkcjhbsdkjchasdkhjbasfybewqbofakheiabwebawefbkbwqejbkawebuhqwebkjqbwadsfkcjhbsdkjchkjdbsckjdbscasdkhjbasfybewqbofakheiabwebawefbkbwqejbkawebuhqwebkjqbwadsfkcjhbsdkjchkjdbscasdkhjbasfybewqbofakheiabwebawefbkbwqejbkawebuhqwebkjqbwafdgsfgsfdgsdfgdasfgsdfgdfgsdfgsfdgsfdgsafgdsfvs4245634635463546dsfkcjhbsdkjchasdkhjbasfybewqbofakheiabwebawefbkbwqejbkawebuhqwebkjqbwadsfkcjhbsdkjchkjdbsckjdbscasdkhjbasfybewqbofakheiabwebawefbkbwqejbkawebuhqwebkjqbwadsfkcjhbsdkjchkjdbsc";
    private final static long JWT_TOKEN_VALIDITY = 60 * 60;

    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getSubject();
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY.getBytes()).build().parseClaimsJws(token).getBody();
    }

    public boolean isTokenExpired(String token) {
        Claims claims = getClaimsFromToken(token);
        Date expDate = claims.getExpiration();
        return expDate.before(new Date());
    }

    public String generateToken(UserDetails userDetails) {

        Map<String, String> claims = new HashMap<>();
        String token = Jwts.builder().addClaims(claims).setSubject(userDetails.getUsername())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000))
                .issuedAt(new Date(System.currentTimeMillis()))
                .signWith(new SecretKeySpec(SECRET_KEY.getBytes(), SignatureAlgorithm.HS512.getJcaName()),
                        SignatureAlgorithm.HS512)
                .compact();
        System.out.println(token);
        return token;
    }

}
