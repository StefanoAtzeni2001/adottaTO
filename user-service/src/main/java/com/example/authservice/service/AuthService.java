package com.example.authservice.service;

import com.example.authservice.dto.AuthRegisterResponseDTO;
import com.example.authservice.dto.AuthRegisterRequestDTO;
import com.example.authservice.model.Auth;
import com.example.authservice.model.UserProfile;
import com.example.authservice.repository.AuthRepository;
import com.example.authservice.repository.UserProfileRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

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

    @Transactional
    public AuthRegisterResponseDTO register(AuthRegisterRequestDTO request) {
        validateEmailNotRegistered(request.getEmail());

        Auth savedAuth = saveNewAuth(request);
        saveUserProfile(savedAuth, request);

        return AuthRegisterResponseDTO.builder()
                .id(savedAuth.getId())
                .email(savedAuth.getEmail())
                .build();
    }

    @Transactional
    public void registerGoogleUserIfNecessary(String email, String name, String surname, String picture) {
        Auth auth = findOrCreateAuthByEmail(email);
        userProfileRepository.findById(auth.getId())
                .orElseGet(() -> saveGoogleUserProfile(auth, email, name, surname, picture));
    }

    public Optional<Auth> findByEmail(String email) {
        return authRepository.findByEmail(email);
    }

    public Optional<Auth> findById(Long id) {
        return authRepository.findById(id);
    }

    public UserProfile getUserProfileByEmail(String email) {
        Auth auth = authRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utente non trovato: " + email));
        return userProfileRepository.findById(auth.getId())
                .orElseThrow(() -> new RuntimeException("Profilo non trovato per utente: " + email));
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

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

    private UserProfile saveGoogleUserProfile(Auth auth, String email, String name, String surname, String picture) {
        UserProfile profile = new UserProfile();
        profile.setAuth(auth);
        profile.setEmail(email);
        profile.setName(name);
        profile.setSurname(surname);
        profile.setProfilePicture(picture);
        return userProfileRepository.save(profile);
    }
}
