package org.example.adoptionpostservice.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO used to transfer full details of an AdoptionPost.
 * Used for create, update, and detail views.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AdoptionPostDetailDto {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime publicationDate;
    private String species;
    private String breed;
    private String gender;
    private Integer age;// in months
    private String color;
    private String location;
    private Long ownerId;
    private Boolean active;
    private Long adopterId;
    private String imageBase64;
}
