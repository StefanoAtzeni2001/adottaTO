package com.example.authservice.service;

import com.example.authservice.dto.AuthRegisterResponseDTO;
import com.example.authservice.dto.AuthRegisterRequestDTO;
import com.example.authservice.model.Auth;
import com.example.authservice.repository.AuthRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class AuthService implements UserDetailsService {

    private final AuthRepository repo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AuthRepository repo,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.repo = repo;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Auth auth = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + email));

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
        if (repo.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("L'email è già registrata.");
        }

        // salva in auth (email e password)
        Auth user = new Auth();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        Auth savedAuth = repo.save(user);

        return AuthRegisterResponseDTO.builder()
                .id(savedAuth.getId())
                .email(savedAuth.getEmail())
                .build();
    }
}
