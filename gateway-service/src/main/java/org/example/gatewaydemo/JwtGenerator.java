package org.example.gatewaydemo;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
// classe a parte per generare un token jwt di prova
public class JwtGenerator {
    public static void main(String[] args) {
        // Segreto condiviso (deve essere lungo almeno 32 caratteri per HS256)
        String secret = "una-chiave-segreta-molto-lunga123456789";
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        // Imposta i dati del payload
        String jwt = Jwts.builder()
                .setSubject("13") // `sub` claim
                .setExpiration(new Date(System.currentTimeMillis() + 7200_000)) // 2 ore
                .signWith(key, SignatureAlgorithm.HS256) // Firma con HS256
                .compact();

        System.out.println("JWT generato:");
        System.out.println(jwt);
    }
}

