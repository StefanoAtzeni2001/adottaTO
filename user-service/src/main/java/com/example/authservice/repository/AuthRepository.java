package com.example.authservice.repository;

import com.example.authservice.model.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing Auth entities.
 * Provides basic CRUD operations via JpaRepository
 * and a method to find an Auth by email.
 */
public interface AuthRepository extends JpaRepository<Auth, Long> {

    /**
     * Finds an Auth entity by its email.
     *
     * @param email the email address to search for
     * @return an Optional containing the found Auth or empty if none found
     */
    Optional<Auth> findByEmail(String email);
    Optional<Auth> findById(Long id);
}
