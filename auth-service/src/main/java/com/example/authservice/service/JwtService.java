package com.example.authservice.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

/**
 * Service class for generating and validating JSON Web Tokens (JWT).
 * Uses a secret key for signing tokens and handles token expiration.
 */
@Service
public class JwtService {

    // Token expiration time in milliseconds (1 day)
    private static final long EXPIRATION_TIME = 86400000;

    // Secret key for signing JWTs (should be long and secure)
    private static final String SECRET = "super-secret-key-for-signing-jwt-which-should-be-long";

    // Key object generated from the secret for signing and parsing JWTs
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    /**
     * Generates a JWT token containing the email as the subject,
     * issued at the current time and expiring after EXPIRATION_TIME.
     *
     * @param email the email to include in the token subject
     * @return a signed JWT token string
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Extracts the email (subject) from a given JWT token.
     *
     * @param token the JWT token string
     * @return the email contained in the token's subject
     */
    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * Validates the token by checking if the email matches
     * and if the token is not expired.
     *
     * @param token the JWT token string
     * @param email the email to compare with the token's subject
     * @return true if valid, false otherwise
     */
    public boolean isTokenValid(String token, String email) {
        String tokenEmail = extractEmail(token);
        return tokenEmail.equals(email) && !isTokenExpired(token);
    }

    /**
     * Checks whether the token is expired by comparing
     * the expiration date to the current date.
     *
     * @param token the JWT token string
     * @return true if expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }
}
