package com.example.authservice.dto;

import lombok.*;

/**
 * Data Transfer Object used to send a JWT
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class JwtResponseDTO {
    private String token;
    private Long userId;
}
