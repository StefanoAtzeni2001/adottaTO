package com.example.authservice.repository;

import com.example.authservice.model.Auth;
import com.example.authservice.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing UserProfile entities.
 * Provides CRUD operations via JpaRepository
 * and a method to find a UserProfile by email.
 */
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
}
