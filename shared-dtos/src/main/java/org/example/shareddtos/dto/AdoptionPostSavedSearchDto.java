package org.example.shareddtos.dto;

import lombok.Data;

import java.util.List;

/**
 * DTO used to request a filtered list of AdoptionPost entities.
 * Contains optional filtering criteria.
 */
@Data
public class AdoptionPostSavedSearchDto {
    private Long id;
    private List<String> species;
    private List<String> breed;
    private String gender;
    private Integer minAge;// in months
    private Integer maxAge;// in months
    private List<String> color;
    private List<String> location;
    private Boolean activeOnly;
}