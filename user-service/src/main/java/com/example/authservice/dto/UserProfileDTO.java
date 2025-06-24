package com.example.authservice.dto;

import lombok.*;

/**
 * Data Transfer Object used to encapsulate user profile data.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserProfileDTO {
    private String name;
    private String surname;
    private String email;
    private String profilePicture;
}

