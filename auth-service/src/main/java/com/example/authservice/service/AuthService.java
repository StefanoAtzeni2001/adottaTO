package com.example.authservice.service;

import com.example.authservice.dto.AuthDTO;
import com.example.authservice.dto.AuthRegisterRequest;
import com.example.authservice.model.Auth;
import com.example.authservice.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private AuthRepository repo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Auth auth = repo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato: " + email));

        return new org.springframework.security.core.userdetails.User(
                auth.getEmail(),
                auth.getPassword(),
                Collections.emptyList() // oppure ruoli se implementati
        );
    }

    @Transactional
    public AuthDTO register(AuthRegisterRequest request) {
        if (repo.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("L'email è già registrata.");
        }

        Auth user = new Auth();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Auth saved = repo.save(user);

        return AuthDTO.builder()
                .id(saved.getId())
                .email(saved.getEmail())
                .password(saved.getPassword()) // di solito si omette nel DTO per sicurezza
                .build();
    }
}
