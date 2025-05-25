package com.example.authservice.dto;

import lombok.Data;

@Data
public class AuthRegisterRequestDTO {
    private String email;
    private String password;
    private String nome;
    private String cognome;
}