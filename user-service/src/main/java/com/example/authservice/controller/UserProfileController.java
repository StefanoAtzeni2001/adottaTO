package com.example.authservice.controller;

import com.example.authservice.dto.EmailRequestDTO;
import com.example.authservice.dto.EmailResponseDTO;
import com.example.authservice.dto.UserProfileDTO;
import com.example.authservice.model.Auth;
import com.example.authservice.model.UserProfile;
import com.example.authservice.repository.UserProfileRepository;
import com.example.authservice.service.AuthService;
import com.example.authservice.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static com.example.authservice.constants.AuthEndpoints.*;

/**
 * Controller per visualizzare e modificare il profilo dell'utente autenticato.
 */
@Controller
public class UserProfileController {

    private final UserProfileRepository userProfileRepository;
    private final AuthService authService;
    private final JwtService jwtService;

    public UserProfileController(JwtService jwtService,
                                 AuthService authService,
                                 UserProfileRepository userProfileRepository) {
        this.jwtService = jwtService;
        this.authService = authService;
        this.userProfileRepository = userProfileRepository;
    }

    @GetMapping(PROFILE)
    public ResponseEntity<?> getUserProfile(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token mancante o malformato");
        }

        String token = authHeader.substring(7);
        if (!jwtService.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Token non valido");
        }

        String userId = jwtService.extractUserId(token);
        Auth user = authService.findById(Long.parseLong(userId)).orElse(null);
        if (user == null) return ResponseEntity.status(404).body("User not found");

        UserProfile profile = authService.getUserProfileByEmail(user.getEmail());
        UserProfileDTO dto = new UserProfileDTO(
                profile.getName(),
                profile.getSurname(),
                profile.getEmail(),
                profile.getProfilePicture()
        );
        return ResponseEntity.ok(dto);
    }


    @PostMapping(API_PROFILE_UPDATE)
    @Transactional
    public ResponseEntity<?> updateUserProfileViaApi(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestBody UserProfileDTO updateRequest) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token mancante o malformato");
        }

        String token = authHeader.substring(7);
        if (!jwtService.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Token non valido");
        }

        // 👇 Usa correttamente l'ID per trovare l'utente
        Long userId = Long.parseLong(jwtService.extractUserId(token));
        Auth user = authService.findById(userId).orElse(null);
        if (user == null) return ResponseEntity.status(404).body("Utente non trovato");

        UserProfile profile = authService.getUserProfileByEmail(user.getEmail());
        if (profile == null) {
            return ResponseEntity.status(404).body("Profilo non trovato");
        }

        profile.setName(updateRequest.getName());
        profile.setSurname(updateRequest.getSurname());
        profile.setProfilePicture(updateRequest.getProfilePicture()); // opzionale

        userProfileRepository.save(profile);

        return ResponseEntity.ok("Profilo aggiornato con successo");
    }



    //rotta per il servizio email
    @PostMapping(PROFILE_EMAIL)
    public ResponseEntity<?> getUserProfileNoToken(@RequestBody EmailRequestDTO request) {

        UserProfile profile = userProfileRepository.findById(request.getUserId()).orElse(null);
        //if (profile == null) return ResponseEntity.status(404).body("User not found");
        EmailResponseDTO dto;
        if (profile == null) {
            dto = new EmailResponseDTO(
                    "Eva",
                    "Fiori",
                    "evina2.ef@gmail.com"
            );
        }
        else{
            dto = new EmailResponseDTO(
                    profile.getName(),
                    profile.getSurname(),
                    profile.getEmail()
            );
        }
        System.out.println(dto.getEmail() + dto.getName() + dto.getSurname());
        return ResponseEntity.ok(dto);
    }

}
