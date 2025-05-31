package com.example.authservice.controller;

import com.example.authservice.dto.AuthRegisterRequestDTO;
import com.example.authservice.service.AuthService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

    /**
     * Constructor for dependency injection of the authentication service.
     *
     * @param authService the authentication service to be used
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
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
