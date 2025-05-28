package org.example.adoptionpostservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "adoption_posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdoptionPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private LocalDateTime publicationDate;
    private String species;
    private String breed;
    private String gender;
    private Integer age; // in months
    private String color;
    private Long ownerId;

}