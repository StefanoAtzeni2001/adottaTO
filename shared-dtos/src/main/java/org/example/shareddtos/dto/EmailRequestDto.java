package org.example.shareddtos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO used to request user's data
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmailRequestDto {
    private Long userId;
}
