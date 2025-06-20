package it.unito.emailservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class EmailResponseDTO {
    private String name;
    private String surname;
    private String email;
}
