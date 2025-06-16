package com.example.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object used as a response after successful user registration.
 * <p>
 * It contains basic user information such as
 * - id: the unique identifier of the registered user
 * - email: the registered email address
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthRegisterResponseDTO {

    private Long id;      // Unique user identifier
    private String email; // Registered email address
}
