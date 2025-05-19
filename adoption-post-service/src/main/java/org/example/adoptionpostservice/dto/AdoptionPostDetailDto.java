package org.example.adoptionpostservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdoptionPostDetailDto {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime publicationDate;
    private String species;
    private String breed;
    private String gender;
    private Integer age;
    private String color;
    private Long ownerId;
}
