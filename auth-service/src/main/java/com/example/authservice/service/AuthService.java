package com.example.authservice.service;

import com.example.authservice.dto.AuthRegisterResponseDTO;
import com.example.authservice.dto.AuthRegisterRequestDTO;
import com.example.authservice.model.Auth;
import com.example.authservice.model.UserProfile;
import com.example.authservice.repository.AuthRepository;
import com.example.authservice.repository.UserProfileRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class AuthService implements UserDetailsService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileRepository userProfileRepository;
    private final JwtService jwtService;

    public AuthService(AuthRepository authRepository,
                       PasswordEncoder passwordEncoder,
                       UserProfileRepository userProfileRepository,
                       JwtService jwtService) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
        this.userProfileRepository = userProfileRepository;
        this.jwtService = jwtService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Auth auth = authRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + email));

        if ("google".equals(auth.getProvider())) {
            throw new UsernameNotFoundException(
                    "Usa il login con Google per questo account");
        }

        // Crea un oggetto UserDetails da passare al token generator
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                auth.getEmail(),
                auth.getPassword(),
                Collections.emptyList()
        );

        // Genera il token
        String token = jwtService.generateToken(userDetails.getUsername());

        // Stampa il token nel log
        System.out.println("Token generato: " + token);

        return new org.springframework.security.core.userdetails.User(
                auth.getEmail(),
                auth.getPassword(),
                Collections.emptyList() // oppure ruoli se implementati
        );
    }

    @Transactional
    public AuthRegisterResponseDTO register(AuthRegisterRequestDTO request) {
        if (authRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("L'email è già registrata.");
        }

        // salva in auth (email e password)
        Auth user = new Auth();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setProvider("local");
        Auth savedAuth = authRepository.save(user);

        UserProfile profile = new UserProfile();
        profile.setAuth(savedAuth);
        profile.setEmail(request.getEmail());
        profile.setName(request.getName());
        profile.setSurname(request.getSurname());
        userProfileRepository.save(profile);

        return AuthRegisterResponseDTO.builder()
                .id(savedAuth.getId())
                .email(savedAuth.getEmail())
                .build();
    }

    @Transactional
    public Auth findOrCreateAuthByEmail(String email) {
        return authRepository.findByEmail(email).orElseGet(() -> {
            Auth auth = new Auth();
            auth.setEmail(email);
            auth.setPassword("oauth2user"); // non usata
            auth.setProvider("google");
            return authRepository.save(auth);
        });
    }
}
