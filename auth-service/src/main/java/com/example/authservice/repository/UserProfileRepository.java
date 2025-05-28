package com.example.authservice.repository;

import com.example.authservice.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//Permette di gestire la memorizzazione in DB attraverso Jpa
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByEmail(String email);
}
