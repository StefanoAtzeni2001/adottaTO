package org.example.adoptionpostservice.dto;

import lombok.Data;

@Data
public class AdoptionPostListDto {
    private Long id;
    private String name;
    private String species;
    private String breed;
    private Integer age;
    private String gender;
    private String color;
}