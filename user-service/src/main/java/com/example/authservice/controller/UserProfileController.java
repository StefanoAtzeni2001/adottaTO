package com.example.authservice.controller;

import org.example.shareddtos.dto.EmailRequestDto;
import com.example.authservice.dto.UserProfileDTO;
import com.example.authservice.model.Auth;
import com.example.authservice.model.UserProfile;
import com.example.authservice.repository.UserProfileRepository;
import com.example.authservice.service.AuthService;
import com.example.authservice.service.JwtService;
import com.example.authservice.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.example.shareddtos.dto.EmailResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Base64;
import static com.example.authservice.constants.AuthEndpoints.*;

/**
 * Controller responsible for retrieving and updating the authenticated user's profile.
 */
@Controller
public class UserProfileController {

    private final UserProfileRepository userProfileRepository;
    private final AuthService authService;
    private final ProfileService profileService;
    private final JwtService jwtService;

    public UserProfileController(JwtService jwtService,
                                 AuthService authService,
                                 UserProfileRepository userProfileRepository, ProfileService profileService) {
        this.jwtService = jwtService;
        this.authService = authService;
        this.userProfileRepository = userProfileRepository;
        this.profileService = profileService;
    }

    /**
     * Returns the profile of the currently authenticated user using a JWT in the Authorization header.
     *
     * @param authHeader Bearer token (JWT)
     * @return user's profile data or appropriate error
     */
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
                profile.getId(),
                profile.getName(),
                profile.getSurname(),
                profile.getEmail(),
                profile.getProfilePicture()
        );
        return ResponseEntity.ok(dto);
    }

    /**
     * Updates the user's profile
     
     * @param updateRequest data to update (name, surname, profile picture)
     * @return success or error response
     */
    @PostMapping(value = API_PROFILE_UPDATE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<?> updateUserProfileViaApi(
            @RequestPart("request") @Valid UserProfileDTO updateRequest,
            @RequestPart(value = "image", required = false) MultipartFile imageFile,
            @RequestHeader("User-Id") Long userId) {
        try {
            String base64Image = null;
            if (imageFile != null && !imageFile.isEmpty()) {
                base64Image = Base64.getEncoder().encodeToString(imageFile.getBytes());
                System.out.println(base64Image);
                updateRequest.setProfilePicture(base64Image);
            }
            profileService.update(updateRequest,userId);
            return ResponseEntity.ok("Profilo aggiornato con successo");
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore durante l'elaborazione dell'immagine");
        }
    }

    /**
     * Returns a user profile by user ID.
     *
     * @param id user ID
     * @return profile data or 404 if not found
     */
    @GetMapping(GET_USER_BY_ID)
    public ResponseEntity<?> getUserProfileById(@PathVariable Long id) {
      
        UserProfile profile = userProfileRepository.findById(id).orElse(null);
        if (profile == null)
            return ResponseEntity.status(404).body("User not found");
        else{
            UserProfileDTO dto = new UserProfileDTO(
                    profile.getName(),
                    profile.getSurname(),
                    profile.getEmail(),
                    profile.getProfilePicture()
            );
            return ResponseEntity.ok(dto);
        }
    }

    /**
     * Provides user profile data (name, surname, email) by user ID,
     * for use by external services like the email service.
     *
     * @param request contains the user ID
     * @return basic profile information, or 404 if not found
     */
    @PostMapping(PROFILE_EMAIL)
    public ResponseEntity<?> getUserProfileNoToken(@RequestBody EmailRequestDto request) {

        UserProfile profile = userProfileRepository.findById(request.getUserId()).orElse(null);
        //if (profile == null) return ResponseEntity.status(404).body("User not found");
        EmailResponseDto dto;
        if (profile == null) {
            dto = new EmailResponseDto(
                    "Eva",
                    "Fiori",
                    //"evina2.ef@gmail.com"*
                    "prova@prova.com"
            );
        }
        else{
            dto = new EmailResponseDto(
                    profile.getName(),
                    profile.getSurname(),
                    profile.getEmail()
            );
        }
        System.out.println(dto.getEmail() + dto.getName() + dto.getSurname());
        return ResponseEntity.ok(dto);
    }

}
