package com.example.authservice.service;

import com.example.authservice.dto.AuthRegisterRequestDTO;
import com.example.authservice.model.Auth;
import com.example.authservice.model.UserProfile;
import com.example.authservice.repository.AuthRepository;
import com.example.authservice.repository.UserProfileRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Base64;
import java.util.Optional;

/**
 * Service responsible for handling user authentication and registration logic
 */
@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileRepository userProfileRepository;

    public AuthService(AuthRepository authRepository,
                       PasswordEncoder passwordEncoder,
                       UserProfileRepository userProfileRepository) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
        this.userProfileRepository = userProfileRepository;
    }

    /**
     * Registers a new user with local credentials
     *
     * @param request the registration request DTO
     * @throws IllegalArgumentException if the email is already registered
     */
    @Transactional
    public void register(AuthRegisterRequestDTO request) {
        validateEmailNotRegistered(request.getEmail());

        Auth savedAuth = saveNewAuth(request);
        saveUserProfile(savedAuth, request);
    }

    /**
     * Registers a new user via Google OAuth2 if they do not already exist
     *
     * @param email   the user's email from Google
     * @param name    the user's first name
     * @param surname the user's last name
     * @param picture the user's profile picture URL
     */
    @Transactional
    public void registerGoogleUserIfNecessary(String email, String name, String surname, String picture) {
        Auth auth = findOrCreateAuthByEmail(email);
        userProfileRepository.findById(auth.getId())
                .orElseGet(() -> saveGoogleUserProfile(auth, email, name, surname, picture));
    }

    /**
     * Finds an Auth entity by email.
     *
     * @param email the email to look up
     * @return an Optional containing the Auth entity if found
     */
    public Optional<Auth> findByEmail(String email) {
        return authRepository.findByEmail(email);
    }

    /**
     * Finds an Auth entity by ID.
     *
     * @param id the user ID
     * @return an Optional containing the Auth entity if found
     */
    public Optional<Auth> findById(Long id) {
        return authRepository.findById(id);
    }

    /**
     * Retrieves the UserProfile associated with a given email.
     *
     * @param email the email of the user
     * @return the corresponding UserProfile
     * @throws RuntimeException if user or profile is not found
     */
    public UserProfile getUserProfileByEmail(String email) {
        Auth auth = authRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato: " + email));
        return userProfileRepository.findById(auth.getId())
                .orElseThrow(() -> new RuntimeException("Profilo non trovato per utente: " + email));
    }

    /**
     * Verifies whether the raw password matches the encoded one.
     *
     * @param rawPassword     the plain password input
     * @param encodedPassword the hashed password stored in DB
     * @return true if passwords match, false otherwise
     */
    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // Private helper methods

    private void validateEmailNotRegistered(String email) {
        if (authRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email già registrata");
        }
    }

    private Auth saveNewAuth(AuthRegisterRequestDTO request) {
        Auth user = new Auth();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setProvider("local");
        return authRepository.save(user);
    }

    private void saveUserProfile(Auth savedAuth, AuthRegisterRequestDTO request) {
        UserProfile profile = new UserProfile();
        profile.setAuth(savedAuth);
        profile.setEmail(request.getEmail());
        profile.setName(request.getName());
        profile.setSurname(request.getSurname());
        profile.setProfilePicture(request.getProfilePicture());
        userProfileRepository.save(profile);
    }

    private Auth findOrCreateAuthByEmail(String email) {
        return authRepository.findByEmail(email).orElseGet(() -> {
            Auth auth = new Auth();
            auth.setEmail(email);
            auth.setPassword("oauth2user"); // password placeholder
            auth.setProvider("google");
            return authRepository.save(auth);
        });
    }

    private UserProfile saveGoogleUserProfile(Auth auth, String email, String name, String surname, String pictureUrl) {
        UserProfile profile = new UserProfile();
        profile.setAuth(auth);
        profile.setEmail(email);
        profile.setName(name);
        profile.setSurname(surname);

        if (pictureUrl != null && !pictureUrl.isEmpty()) {
            try (InputStream in = new URL(pictureUrl).openStream()) {
                String base64Image = Base64.getEncoder().encodeToString(in.readAllBytes());
                profile.setProfilePicture(base64Image);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }

        return userProfileRepository.save(profile);
    }
}
