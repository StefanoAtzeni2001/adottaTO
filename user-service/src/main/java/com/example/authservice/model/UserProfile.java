package com.example.authservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity representing a user's profile information.
 * Linked to the Auth entity via a shared primary key (one-to-one mapping).
 */
@Entity
@Table(name = "user_profile")
@Getter
@Setter
public class UserProfile {

    /**
     * Primary key, shared with the corresponding Auth entity.
     * Acts as both the ID and foreign key to Auth.
     */
    @Id
    private Long id;

    /**
     * One-to-one relationship with the Auth entity.
     * Uses the same ID as the primary key.
     */
    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Auth auth;

    /**
     * User email must be unique and not null.
     * Typically, matches the email in the Auth entity.
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * User's first name.
     */
    @Column(nullable = false)
    private String name;

    /**
     * User's last name.
     */
    @Column(nullable = false)
    private String surname;

    @Column(nullable = true)
    private String profilePicture;
}
