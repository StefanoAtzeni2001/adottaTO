package com.example.authservice.config;

import com.example.authservice.model.Auth;
import com.example.authservice.model.UserProfile;
import com.example.authservice.repository.AuthRepository;
import com.example.authservice.repository.UserProfileRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.util.Base64;

@Configuration
public class DataInitializer {

    @Bean
    @Transactional
    public CommandLineRunner initData(AuthRepository authRepository,
                                      UserProfileRepository userProfileRepository,
                                      PasswordEncoder passwordEncoder) {
        return args -> {
            if (authRepository.count() == 0) {

                // Utente 1
                createAndSaveUser("mario@mail.com", "mario", "Mario", "Rossi", "Mario.jpg",
                        authRepository, userProfileRepository, passwordEncoder);

                // Utente 2
                createAndSaveUser("anna@mail.com", "anna", "Anna", "Bianchi", "Anna.jpg",
                        authRepository, userProfileRepository, passwordEncoder);

                // Utente 3
                createAndSaveUser("luca@mail.com", "luca", "Luca", "Verdi", "Luca.jpg",
                        authRepository, userProfileRepository, passwordEncoder);

                // Utente 4
                createAndSaveUser("elena@mail.com", "elena", "Elena", "Neri", "Elena.jpg",
                        authRepository, userProfileRepository, passwordEncoder);

                System.out.println(">>> 4 utenti demo creati.");
            }
        };
    }

    private void createAndSaveUser(String email, String rawPassword, String name, String surname, String imageFileName,
                                   AuthRepository authRepository,
                                   UserProfileRepository userProfileRepository,
                                   PasswordEncoder passwordEncoder) {
        Auth auth = new Auth();
        auth.setEmail(email);
        auth.setPassword(passwordEncoder.encode(rawPassword));
        auth.setProvider("local");

        UserProfile profile = new UserProfile();
        profile.setEmail(email);
        profile.setName(name);
        profile.setSurname(surname);
        profile.setProfilePicture(encodeImage(imageFileName));
        profile.setAuth(auth);

        userProfileRepository.save(profile);
        System.out.println("Utente creato: " + email + " / " + rawPassword);
    }

    private String encodeImage(String fileName) {
        try {
            ClassPathResource imgFile = new ClassPathResource("images/" + fileName);
            byte[] bytes = StreamUtils.copyToByteArray(imgFile.getInputStream());
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
