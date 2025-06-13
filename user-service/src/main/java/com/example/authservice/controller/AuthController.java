package com.example.authservice.controller;

import com.example.authservice.dto.AuthRegisterRequestDTO;
import com.example.authservice.dto.JwtResponseDTO;
import com.example.authservice.dto.LoginRequestDTO;
import com.example.authservice.service.AuthService;
import com.example.authservice.service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static com.example.authservice.constants.AuthEndpoints.*;

/**
 * Controller responsible for handling authentication-related operations:
 * - Custom login page
 * - Manual registration
 * - Google OAuth2 registration
 */
@Controller
public class AuthController {

    private final AuthService authService;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    /**
     * Constructor for dependency injection of the authentication service.
     *
     * @param authService the authentication service to be used
     */
    public AuthController(AuthService authService,
                          AuthenticationManager authenticationManager,
                          JwtService jwtService) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @PostMapping(API_LOGIN)
    @ResponseBody
    public ResponseEntity<?> apiLogin(
            @RequestBody LoginRequestDTO request,
            HttpServletResponse response
    ) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            // imposta cookie HttpOnly
            response.addHeader("Set-Cookie", "oauth_email=" + request.getEmail() + "; Path=/; HttpOnly; SameSite=Lax");

            // risponde con l’URL da seguire
            return ResponseEntity.ok().body(Map.of("redirectUrl", "http://localhost:3000/oauth-redirect"));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping(API_REGISTER)
    @ResponseBody
    public ResponseEntity<?> registerViaApi(@RequestBody AuthRegisterRequestDTO request) {
        try {
            authService.register(request);
            return ResponseEntity.ok("Utente registrato con successo");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email già registrata");
        }
    }

    @GetMapping(GOOGLE_REGISTRATION)
    @Transactional
    public void registerGoogleUser(OAuth2AuthenticationToken token, HttpServletResponse response) throws IOException {
        String email = token.getPrincipal().getAttribute("email");
        String name = token.getPrincipal().getAttribute("given_name");
        String surname = token.getPrincipal().getAttribute("family_name");

        authService.registerGoogleUserIfNecessary(email, name, surname);

        // Salva l’email in un cookie HttpOnly temporaneo
        String redirectUrl = "http://localhost:3000/oauth-redirect"; // pagina neutra che chiama l'API per ottenere il JWT

        response.addHeader("Set-Cookie", "oauth_email=" + email + "; Path=/; HttpOnly; SameSite=Lax");
        response.sendRedirect(redirectUrl);
    }

    @GetMapping(API_OAUTH_JWT)
    @ResponseBody
    public ResponseEntity<?> getJwtFromOauthCookie(@CookieValue(value = "oauth_email", required = false) String email) {
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Nessun email trovata nel cookie");
        }

        String token = jwtService.generateToken(email);

        return ResponseEntity.ok(new JwtResponseDTO(token));
    }


}
