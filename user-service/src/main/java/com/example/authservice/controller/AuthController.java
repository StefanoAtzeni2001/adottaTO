package com.example.authservice.controller;

import com.example.authservice.dto.AuthRegisterRequestDTO;
import com.example.authservice.dto.JwtResponseDTO;
import com.example.authservice.dto.LoginRequestDTO;
import com.example.authservice.service.AuthService;
import com.example.authservice.service.JwtService;
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

    /**
     * Registers a user authenticated through Google OAuth2, if they don't already exist.
     *
     * @param token the OAuth2 authentication token containing user details
     * @return redirect to the user page after login
     */
    @Transactional
    @GetMapping(GOOGLE_REGISTRATION)
    public String registerGoogleUser(OAuth2AuthenticationToken token) {
        String email = token.getPrincipal().getAttribute("email");
        String name = token.getPrincipal().getAttribute("given_name");
        String surname = token.getPrincipal().getAttribute("family_name");

        authService.registerGoogleUserIfNecessary(email, name, surname);

        return "redirect:" + USER_PAGE;
    }
}
