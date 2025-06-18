package com.example.authservice.config;

import com.example.authservice.model.Auth;
import com.example.authservice.model.UserProfile;
import com.example.authservice.repository.AuthRepository;
import com.example.authservice.repository.UserProfileRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
public class DataInitializer {

    @Bean
    @Transactional
    public CommandLineRunner initData(AuthRepository authRepository,
                                      UserProfileRepository userProfileRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            if (authRepository.count() > 0) {
                return;
            }

            // Crea e salva Auth
            Auth auth = new Auth();
            auth.setEmail("prova@prova.com");
            auth.setPassword(passwordEncoder.encode("prova"));
            auth.setProvider("local");

            // Crea UserProfile e collega Auth
            UserProfile profile = new UserProfile();
            profile.setEmail("prova@prova.com");
            profile.setName("Mario");
            profile.setSurname("Rossi");
            profile.setProfilePicture(null);
            profile.setAuth(auth); // questa linea imposta anche l'id

            // Salva prima il profilo (Auth viene salvato grazie a @MapsId)
            userProfileRepository.save(profile);

            System.out.println("Utente demo creato: prova@prova.com / prova");
        };
    }
}
