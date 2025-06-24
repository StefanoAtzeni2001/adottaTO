package org.example.shareddtos.dto;

import lombok.*;

import java.util.List;
/**
 * DTO used to request a filtered list of AdoptionPost entities.
 * Contains optional filtering criteria.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AdoptionPostSearchDto {
    private List<String> species;
    private List<String> breed;
    private String gender;
    private Integer minAge;// in months
    private Integer maxAge;// in months
    private List<String> color;
    private List<String> location;
    private Boolean activeOnly;
}