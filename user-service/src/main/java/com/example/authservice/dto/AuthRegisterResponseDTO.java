package com.example.authservice.dto;

import lombok.*;

/**
 * Data Transfer Object used as a response after successful user registration.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class AuthRegisterResponseDTO {

    private Long id;      // Unique user identifier
    private String email; // Registered email address
}
