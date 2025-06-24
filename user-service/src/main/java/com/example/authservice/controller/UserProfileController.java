package com.example.authservice.controller;

import com.example.authservice.dto.UserProfileDTO;
import com.example.authservice.service.ProfileService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.example.shareddtos.dto.EmailResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Base64;
import java.util.NoSuchElementException;
import static com.example.authservice.constants.UserEndpoints.*;
/**
 * Controller responsible for retrieving and updating the authenticated user's profile.
 */
@RestController
@RequestMapping("/user")
public class UserProfileController {

    private final ProfileService profileService;

    /**
     * Constructs a new UserProfileController with the given ProfileService.
     *
     * @param profileService the service used to manage user profiles
     */
    public UserProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    /**
     * Retrieves the profile of the authenticated user.
     *
     * @param userId the ID of the authenticated user, passed in the request header
     * @return the user's profile information, or 404 if not found
     */
    @GetMapping(GET_MY_PROFILE)
    public ResponseEntity<UserProfileDTO> getMyUserProfile(@RequestHeader("User-Id") Long userId) {
        try {
            UserProfileDTO dto = profileService.getUserById(userId);
            return ResponseEntity.ok(dto);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieves the profile of a user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return the user's profile information, or 404 if not found
     */
    @GetMapping(GET_USER_BY_ID)
    public ResponseEntity<UserProfileDTO> getUserProfileById(@PathVariable Long id) {
        try {
            UserProfileDTO dto = profileService.getUserById(id);
            return ResponseEntity.ok(dto);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Updates the profile of the authenticated user, including optional profile picture upload.
     *
     * @param updateRequest the updated profile data
     * @param imageFile an optional image file representing the new profile picture
     * @param userId the ID of the authenticated user, passed in the request header
     * @return a success message if the update is successful, or an error message otherwise
     */
    @PostMapping(value = UPDATE_PROFILE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
            profileService.update(updateRequest, userId);
            return ResponseEntity.ok("Profilo aggiornato con successo");
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Errore durante l'elaborazione dell'immagine");
        }
    }

    /**
     * Retrieves only the email address of a user by their ID.
     *
     * @param id the ID of the user to retrieve the email for
     * @return the user's email address, or 404 if not found
     */
    @GetMapping(GET_PROFILE_EMAIL)
    public ResponseEntity<EmailResponseDto> getEmailProfileById(@PathVariable Long id) {
        try {
            EmailResponseDto dto = profileService.getEmailById(id);
            return ResponseEntity.ok(dto);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
