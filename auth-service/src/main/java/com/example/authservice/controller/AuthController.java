package com.example.authservice.controller;


import com.example.authservice.dto.AuthRegisterRequestDTO;
import com.example.authservice.model.Auth;
import com.example.authservice.model.UserProfile;
import com.example.authservice.repository.UserProfileRepository;
import com.example.authservice.service.AuthService;
import com.example.authservice.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
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

    @Autowired
    private UserProfileRepository userProfileRepository;

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

    @Transactional
    @GetMapping("/googleRegistration")
    public String registerGoogleUser(OAuth2AuthenticationToken tokenGoogle) {
        String name = tokenGoogle.getPrincipal().getAttribute("given_name");
        String surname = tokenGoogle.getPrincipal().getAttribute("family_name");
        String email = tokenGoogle.getPrincipal().getAttribute("email");

        Auth auth = authService.findOrCreateAuthByEmail(email);

        // crea profilo se non esiste
        UserProfile profile = userProfileRepository.findById(auth.getId()).orElse(null);
        if (profile == null) {
            profile = new UserProfile();
            profile.setAuth(auth);
            profile.setEmail(email);
            profile.setName(name);
            profile.setSurname(surname);
        }
        userProfileRepository.save(profile);


        // Genera il token
        String token = jwtService.generateToken(email);

        return "redirect:/userpage";
    }

    @GetMapping("/userpage")
    public String userPage(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        String email;

        if (principal instanceof OAuth2AuthenticationToken oauth2Token) {
            email = oauth2Token.getPrincipal().getAttribute("email");
        } else {
            email = principal.getName(); // preso da form login
        }

        // Trova l'entità Auth per ottenere l'id
        Auth auth = authService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato per email: " + email));

        // Trova UserProfile usando l'id
        UserProfile profile = userProfileRepository.findById(auth.getId())
                .orElseThrow(() -> new RuntimeException("Profilo non trovato per id: " + auth.getId()));

        model.addAttribute("name", profile.getName());
        model.addAttribute("surname", profile.getSurname());
        model.addAttribute("email", profile.getEmail());

        return "userpage";
    }

    @GetMapping("/edit-profile")
    public String editProfile(Model model, Principal principal) {
        String email;

        if (principal instanceof OAuth2AuthenticationToken oauthToken) {
            email = oauthToken.getPrincipal().getAttribute("email");
        } else {
            email = principal.getName();
        }

        Auth auth = authService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        UserProfile profile = userProfileRepository.findById(auth.getId())
                .orElseThrow(() -> new RuntimeException("Profilo non trovato"));

        model.addAttribute("user", profile);
        return "edit-profile";
    }

    @PostMapping("/update-profile")
    @Transactional
    public String updateProfile(@RequestParam Long id,
                                @RequestParam String name,
                                @RequestParam String surname) {
        UserProfile profile = userProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profilo non trovato"));

        profile.setName(name);
        profile.setSurname(surname);
        userProfileRepository.save(profile);

        return "redirect:/userpage";
    }

}
