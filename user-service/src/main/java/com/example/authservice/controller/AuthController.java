package com.example.authservice.controller;

import com.example.authservice.dto.AuthRegisterRequestDTO;
import com.example.authservice.dto.JwtResponseDTO;
import com.example.authservice.dto.LoginRequestDTO;
import com.example.authservice.service.AuthService;
import com.example.authservice.service.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    /**
     * Displays the custom login page.
     *
     * @return the name of the login view
     */
    @GetMapping(LOGIN_PAGE)
    public String login() {
        System.out.println("→ Custom login page accessed");
        return "login";
    }

    @PostMapping(LOGIN_PAGE)
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            String token = jwtService.generateToken(request.getEmail());

            return ResponseEntity.ok(new JwtResponseDTO(token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping(LOGIN_API)
    @ResponseBody
    public ResponseEntity<?> apiLogin(@RequestBody LoginRequestDTO request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            String token = jwtService.generateToken(request.getEmail());
            return ResponseEntity.ok(new JwtResponseDTO(token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }


    /**
     * Displays the user registration page.
     *
     * @return the name of the registration view
     */
    @GetMapping(REGISTER_PAGE)
    public String registerForm() {
        System.out.println("→ Registration page accessed");
        return "register";
    }

    @PostMapping("/api/register")
    @ResponseBody
    public ResponseEntity<?> registerViaApi(@RequestBody AuthRegisterRequestDTO request) {
        try {
            authService.register(request);
            return ResponseEntity.ok("Utente registrato con successo");
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email già registrata");
        }
    }


    /**
     * Handles manual user registration using form data.
     *
     * @param email    the email of the new user
     * @param password the password for the new account
     * @param name     the first name of the user
     * @param surname  the last name of the user
     * @return redirect to the login page on success or to the registration page with an error
     */
    @PostMapping(REGISTER_PAGE)
    public String register(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String name,
            @RequestParam String surname) {
        try {
            AuthRegisterRequestDTO request = new AuthRegisterRequestDTO();
            request.setEmail(email);
            request.setPassword(password);
            request.setName(name);
            request.setSurname(surname);

            authService.register(request);
            return "redirect:" + LOGIN_PAGE;
        } catch (IllegalArgumentException ex) {
            return "redirect:" + REGISTER_PAGE + "?error=exists";
        }
    }

    @GetMapping(GOOGLE_REGISTRATION)
    @Transactional
    public void registerGoogleUser(OAuth2AuthenticationToken token, HttpServletResponse response) throws IOException {
        String email = token.getPrincipal().getAttribute("email");
        String name = token.getPrincipal().getAttribute("given_name");
        String surname = token.getPrincipal().getAttribute("family_name");

        authService.registerGoogleUserIfNecessary(email, name, surname);

        String jwt = jwtService.generateToken(email);

        // Redirect al frontend con il token
        String redirectUrl = "http://localhost:3000/userpage?token=" + jwt;
        response.sendRedirect(redirectUrl);
    }
}
