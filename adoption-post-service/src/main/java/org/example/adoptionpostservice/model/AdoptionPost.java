package org.example.adoptionpostservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
/**
 * Entity representing an adoption post.
 * Stores all relevant information about an animal available for adoption.
 */
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
    private String location;
    private Long ownerId;
    private Boolean active;
    private Long adopterId;

}