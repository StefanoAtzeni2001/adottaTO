package com.example.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Data Transfer Object used to encapsulate user registration data.
 * <p>
 * It contains the required fields for a new user to register:
 * email, password, name, and surname.
 */

@Data
public class AuthRegisterRequestDTO {

    private String email;    // User's email address
    private String password; // User's plain text password (will be encoded)
    private String name;     // User's first name
    private String surname;  // User's last name
}