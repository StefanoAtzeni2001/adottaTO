package com.example.authservice.controller;


import com.example.authservice.model.Auth;
import com.example.authservice.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private AuthRepository repo;

    @GetMapping("/login")
    public String login() {
        System.out.println("→ Login personalizzato chiamato");
        return "custom_login";
    }

    @GetMapping("/register")
    public String registerForm() {
        System.out.println("→ Pagina di registrazione chiamata");
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String email, @RequestParam String password) {
        System.out.println("Registrazione utente: " + email + ", " + password);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        Auth auth = new Auth();
        auth.setEmail(email);
        auth.setPassword(encoder.encode(password));

        repo.save(auth);
        return "redirect:/login";
    }

    @GetMapping("/homepage")
    public String homepage() {
        return "homepage"; // caricherà homepage.html
    }
}
