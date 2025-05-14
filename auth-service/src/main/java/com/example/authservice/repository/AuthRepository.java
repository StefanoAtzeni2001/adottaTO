package com.example.authservice.repository;


import com.example.authservice.model.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//Permette di gestire la memorizzazione in DB attraverso Jpa
public interface AuthRepository extends JpaRepository<Auth, Long> {
    Optional<Auth> findByEmail(String email);
}