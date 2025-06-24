package com.example.authservice.controller;

import com.example.authservice.dto.UserProfileDTO;
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
import java.util.NoSuchElementException;
import static com.example.authservice.constants.AuthEndpoints.*;

/**
 * Controller responsible for retrieving and updating the authenticated user's profile.
 */
@Controller
public class UserProfileController {

    private final ProfileService profileService;

    public UserProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }


    @GetMapping(PROFILE)
    public ResponseEntity<?> getMyUserProfile(@RequestHeader("User-Id") Long userId) {
            try {
                UserProfileDTO dto = profileService.getUserById(userId);
                return ResponseEntity.ok(dto);
            } catch (NoSuchElementException e) {
                return ResponseEntity.notFound().build();
            }
        }


    @GetMapping(GET_USER_BY_ID)
    public ResponseEntity<?> getUserProfileById(@PathVariable Long id) {
        try {
            UserProfileDTO dto = profileService.getUserById(id);
            return ResponseEntity.ok(dto);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }



    @PostMapping(value = API_PROFILE_UPDATE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Transactional
    public ResponseEntity<?> updateUserProfile(
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


    @GetMapping(PROFILE_EMAIL)
    public ResponseEntity<?> getEmailProfileById(@PathVariable Long id) {
        try {
            EmailResponseDto dto = profileService.getEmailById(id);
            return ResponseEntity.ok(dto);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
