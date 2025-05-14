package com.example.authservice.dto;

import lombok.Data;

@Data
public class AuthRegisterRequest {
    private String email;
    private String password;
}