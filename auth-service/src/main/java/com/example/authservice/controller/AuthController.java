package com.example.authservice.controller;


import com.example.authservice.dto.AuthRegisterRequest;
import com.example.authservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

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
    public String register(@RequestParam String email, @RequestParam String password) {
        try {
            AuthRegisterRequest request = new AuthRegisterRequest();
            request.setEmail(email);
            request.setPassword(password);

            authService.register(request);
            return "redirect:/login";
        } catch (IllegalArgumentException ex) {
            return "redirect:/register?error=exists";
        }
    }


    @GetMapping("/homepage")
    public String homepage() {
        return "homepage"; // caricherà homepage.html
    }
}
