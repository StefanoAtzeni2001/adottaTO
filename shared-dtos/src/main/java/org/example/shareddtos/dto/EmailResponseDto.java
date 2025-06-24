package org.example.shareddtos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO used to response user's data
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmailResponseDto {
    private String name;
    private String surname;
    private String email;
}
