package com.example.authservice.controller;


import com.example.authservice.dto.AuthRegisterRequestDTO;
import com.example.authservice.service.AuthService;
import com.example.authservice.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class AuthController {

    @Autowired
    private JwtService jwtService;

    @GetMapping("/login")
    public String login() {
        System.out.println("→ Login personalizzato chiamato");
        return "login";
    }

    @GetMapping("/register")
    public String registerForm() {
        System.out.println("→ Pagina di registrazione chiamata");
        return "register";
    }

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
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
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            return "redirect:/register?error=exists";
        }
    }

    @GetMapping("/profile")
    public String profile(OAuth2AuthenticationToken token, Model model) {
        model.addAttribute("name", token.getPrincipal().getAttribute("name"));
        model.addAttribute("email", token.getPrincipal().getAttribute("email"));
        model.addAttribute("picture", token.getPrincipal().getAttribute("picture"));
        return "user-profile";
    }

    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }
    @GetMapping("/homepage")
    public String homepage() {
        return "homepage"; // caricherà homepage.html
    }
}
