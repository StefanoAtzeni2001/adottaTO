package com.example.sessionmanager.controller;


import com.example.sessionmanager.dto.AuthDTO;
import com.example.sessionmanager.model.Data;
import com.example.sessionmanager.repository.DataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

@Controller
public class AppController {

    @Autowired
    private DataRepository repo;

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

        Data data = new Data();
        data.setEmail(email);
        data.setPassword(encoder.encode(password));

        repo.save(data);
        return "redirect:/login";
    }
}
