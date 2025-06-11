package org.example.adoptionpostservice.dto;
import lombok.Data;

/**
 * DTO used to transfer summary information of an AdoptionPost.
 * used in list views.
 */
@Data
public class AdoptionPostSummaryDto {
    private Long id;
    private String name;
    private String species;
    private String breed;
    private Integer age;// in months
    private String gender;
    private String color;
    private Boolean active;
}