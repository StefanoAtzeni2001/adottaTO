package com.example.sessionmanager.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AppController {

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
        // Qui puoi aggiungere salvataggio nel database, validazioni ecc.
        return "redirect:/login";
    }

    /*

    @Autowired
    private DataRepository repo;


    @PostMapping("/send")
    public ResponseEntity<String> send(@RequestParam String content) {
        repo.save(new Data(content));// Salva il dato in schema_a
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.postForObject("http://serviceb:8080/B/recive", content, String.class); //manda il dato a service_b notare url mappato interno
        return ResponseEntity.ok("Saved in A and sended to B");
    }
    */
}
