package com.example.authservice.dto;

import lombok.*;

/**
 * Data Transfer Object used to encapsulate user authentication data.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class LoginRequestDTO {
    private String email;
    private String password;
    private String id;
}
