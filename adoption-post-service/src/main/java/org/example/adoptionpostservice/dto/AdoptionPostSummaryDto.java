package org.example.adoptionpostservice.dto;
import lombok.Data;

//Dto used to send most important information of an AdoptionPost
@Data
public class AdoptionPostSummaryDto {
    private Long id;
    private String name;
    private String species;
    private String breed;
    private Integer age;
    private String gender;
    private String color;
}