package com.example.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
@AllArgsConstructor
@Data
public class UserProfileDTO {
    private String name;
    private String surname;
    private String email;
    private String profilePicture;
}

