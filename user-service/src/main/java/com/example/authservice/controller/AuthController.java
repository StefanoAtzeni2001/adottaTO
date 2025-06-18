package com.example.authservice.controller;

import com.example.authservice.dto.AuthRegisterRequestDTO;
import com.example.authservice.dto.JwtResponseDTO;
import com.example.authservice.dto.LoginRequestDTO;
import com.example.authservice.model.Auth;
import com.example.authservice.service.AuthService;
import com.example.authservice.service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final JwtService jwtService;

    public AuthController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping(API_LOGIN)
    @ResponseBody
    public ResponseEntity<?> apiLogin(@RequestBody LoginRequestDTO request, HttpServletResponse response) {
        Auth userAuth = authService.findByEmail(request.getEmail()).orElse(null);
        if (userAuth == null || !authService.checkPassword(request.getPassword(), userAuth.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Credenziali non valide"));
        }

        response.addHeader("Set-Cookie", "oauth_user_id=" + userAuth.getId() + "; Path=/; HttpOnly; SameSite=Lax");
        return ResponseEntity.ok().body(Map.of("redirectUrl", "http://localhost:3000/oauth-redirect"));
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
        String profilePicture = token.getPrincipal().getAttribute("picture");

        authService.registerGoogleUserIfNecessary(email, name, surname, profilePicture);

        Auth userAuth = authService.findByEmail(email).orElse(null);
        if (userAuth == null) {
            response.sendRedirect("http://localhost:3000/login");
            return;
        }

        response.addHeader("Set-Cookie", "oauth_user_id=" + userAuth.getId() + "; Path=/; HttpOnly; SameSite=Lax");
        response.sendRedirect("http://localhost:3000/oauth-redirect");
    }

    @GetMapping(API_OAUTH_JWT)
    @ResponseBody
    public ResponseEntity<?> getJwtFromOauthCookie(@CookieValue(value = "oauth_user_id", required = false) String userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Cookie mancante"));
        }

        Long id;
        try {
            id = Long.parseLong(userId);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "ID utente non valido"));
        }

        Auth userAuth = authService.findById(id).orElse(null);
        if (userAuth == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Utente non trovato"));
        }

        String token = jwtService.generateToken(String.valueOf(userAuth.getId()));
        return ResponseEntity.ok(new JwtResponseDTO(token));
    }
}
