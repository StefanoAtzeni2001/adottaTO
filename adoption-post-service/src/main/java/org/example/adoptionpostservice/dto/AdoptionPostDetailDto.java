package org.example.adoptionpostservice.dto;

import lombok.Data;
import java.time.LocalDateTime;
//Dto used to send all information of an AdoptionPost
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
