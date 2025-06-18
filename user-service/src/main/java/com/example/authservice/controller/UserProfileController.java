package com.example.authservice.controller;

import com.example.authservice.dto.UserProfileDTO;
import com.example.authservice.model.UserProfile;
import com.example.authservice.repository.UserProfileRepository;
import com.example.authservice.service.AuthService;
import com.example.authservice.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import static com.example.authservice.constants.AuthEndpoints.API_PROFILE_UPDATE;
import static com.example.authservice.constants.AuthEndpoints.PROFILE;

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

        // Nel JwtService hai extractUserId() invece di extractEmail(), quindi uso extractUserId
        String email = jwtService.extractUserId(token);
        UserProfile profile = authService.getUserProfileByEmail(email);

        if (profile == null) {
            return ResponseEntity.status(404).body("Profilo non trovato");
        }

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

        String email = jwtService.extractUserId(token);
        UserProfile profile = authService.getUserProfileByEmail(email);

        if (profile == null) {
            return ResponseEntity.status(404).body("Profilo non trovato");
        }

        profile.setName(updateRequest.getName());
        profile.setSurname(updateRequest.getSurname());
        profile.setProfilePicture(updateRequest.getProfilePicture()); // opzionale se previsto aggiornare
        userProfileRepository.save(profile);

        return ResponseEntity.ok("Profilo aggiornato con successo");
    }
}
