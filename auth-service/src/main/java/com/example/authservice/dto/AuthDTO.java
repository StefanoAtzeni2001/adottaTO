package com.example.authservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthDTO {
    private Long id;
    private String email;
    private String password;
}
