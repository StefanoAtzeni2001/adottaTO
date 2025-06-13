package com.example.authservice.controller;

import com.example.authservice.dto.UserProfileDTO;
import com.example.authservice.model.Auth;
import com.example.authservice.model.UserProfile;
import com.example.authservice.repository.UserProfileRepository;
import com.example.authservice.service.AuthService;
import com.example.authservice.service.JwtService;
import com.example.authservice.util.SecurityUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import static com.example.authservice.constants.AuthEndpoints.*;

import java.security.Principal;

/**
 * Controller responsible for displaying and editing the authenticated user's profile.
 * It includes functionality for:
 * - Viewing user profile data
 * - Editing user information
 * - Updating stored profile data
 */
@Controller
public class UserProfileController {

    private final UserProfileRepository userProfileRepository;
    private final AuthService authService;
    private final JwtService jwtService;

    /**
     * Constructor with dependencies injected.
     *
     * @param userProfileRepository the repository for accessing user profile data
     * @param authService the authentication service for retrieving authenticated user info
     */
    public UserProfileController(JwtService jwtService, AuthService authService, UserProfileRepository userProfileRepository) {
        this.jwtService = jwtService;
        this.authService = authService;
        this.userProfileRepository = userProfileRepository;
    }

    /**
     * Displays the authenticated user's profile page.
     *
     * @param model     the model to pass user data to the view
     * @param principal an authenticated user
     * @return the user profile view or redirects to the login if not authenticated
     */
    @GetMapping(USER_PAGE)
    public String userPage(Model model, Principal principal) {
        if (principal == null) return "redirect:" + LOGIN_PAGE;

        String email = SecurityUtils.extractEmail(principal);
        UserProfile profile = authService.getUserProfileByEmail(email);

        model.addAttribute("name", profile.getName());
        model.addAttribute("surname", profile.getSurname());
        model.addAttribute("email", profile.getEmail());

        return "userpage";
    }

    /**
     * Displays the edit profile form populated with the user's current data.
     *
     * @param model     the model to pass profile data to the view
     * @param principal an authenticated user
     * @return the edit profile view
     */
    @GetMapping(EDIT_PROFILE_PAGE)
    public String editProfile(Model model, Principal principal) {
        UserProfile profile = getUserProfileFromPrincipal(principal);
        model.addAttribute("user", profile);
        return "edit-profile";
    }

    /**
     * Handles the update of user profile data.
     *
     * @param id      the ID of the user profile
     * @param name    the new name to update
     * @param surname the new surname to update
     * @return redirect to the user profile page after the successful update
     */
    @PostMapping(UPDATE_PROFILE_PAGE)
    @Transactional
    public String updateProfile(@RequestParam Long id,
                                @RequestParam String name,
                                @RequestParam String surname) {
        UserProfile profile = userProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        profile.setName(name);
        profile.setSurname(surname);
        userProfileRepository.save(profile);

        return "redirect:" + USER_PAGE;
    }

    /**
     * Retrieves the user profile based on the authenticated principal.
     *
     * @param principal an authenticated user
     * @return the corresponding UserProfile object
     */
    private UserProfile getUserProfileFromPrincipal(Principal principal) {
        String email = SecurityUtils.extractEmail(principal);
        Auth auth = authService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userProfileRepository.findById(auth.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found"));
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token mancante o malformato");
        }

        String token = authHeader.substring(7);
        if (!jwtService.isTokenValid(token)) {
            return ResponseEntity.status(401).body("Token non valido");
        }

        String email = jwtService.extractEmail(token);
        UserProfile profile = authService.getUserProfileByEmail(email);

        if (profile == null) {
            return ResponseEntity.status(404).body("Profilo non trovato");
        }

        UserProfileDTO dto = new UserProfileDTO(profile.getName(), profile.getSurname(), profile.getEmail());
        return ResponseEntity.ok(dto);
    }
}
