package com.example.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class JwtResponseDTO {
    private String token;
    public JwtResponseDTO(String token) { this.token = token; }
    public String getToken() { return token; }
}
