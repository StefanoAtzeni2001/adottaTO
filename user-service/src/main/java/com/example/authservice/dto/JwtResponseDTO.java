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
    private Long userId;
    public JwtResponseDTO(String token, Long userId) { this.token = token; this.userId = userId;}
    public String getToken() { return token; }
    public String getUserId() {return userId.toString();}
}
