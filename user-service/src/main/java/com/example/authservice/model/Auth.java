package com.example.authservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing authentication credentials for a user.
 * Stores email, hashed password, and the authentication provider type.
 */
@Entity
@Table(name = "auth")
@Getter
@Setter
public class Auth {

    /**
     * Primary key identifier for the auth record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User email must be unique and valid format.
     * Used as the username for authentication.
     */
    @Email
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * Encrypted user password.
     * Not used when the provider is OAuth-based (e.g., Google).
     */
    @Column(nullable = false)
    private String password;

    /**
     * Authentication provider type.
     * Possible values: "local" for standard login, "google" for OAuth2.
     */
    @Column(nullable = false)
    private String provider;
}

