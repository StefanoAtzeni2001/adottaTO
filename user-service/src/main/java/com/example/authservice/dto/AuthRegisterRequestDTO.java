package com.example.authservice.dto;

import lombok.*;

/**
 * Data Transfer Object used to encapsulate user registration data.
 * It contains the required fields for a new user to register:
 * email, password, name, and surname.
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AuthRegisterRequestDTO {

    private String email;    // User's email address
    private String password; // User's plain text password (will be encoded)
    private String name;     // User's first name
    private String surname;  // User's last name
    private String profilePicture;
}