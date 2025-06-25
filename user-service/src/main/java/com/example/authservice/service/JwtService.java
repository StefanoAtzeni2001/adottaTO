package com.example.authservice.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Service class for generating and validating JSON Web Tokens (JWT).
 * Uses a secret key for signing tokens and handles token expiration.
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    /**
     * Expiration time for JWT tokens in milliseconds.
     * Currently set to 24 hours (86400000 ms).
     */
    private static final long EXPIRATION_TIME = 86400000;

    /**
     * Generates a JWT token containing the given user ID as the subject
     *
     * @param userId the user ID to encode in the token's subject claim
     * @return the generated JWT token as a compact String
     */
    public String generateToken(String userId) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        System.out.println("chiave:" + secret);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
