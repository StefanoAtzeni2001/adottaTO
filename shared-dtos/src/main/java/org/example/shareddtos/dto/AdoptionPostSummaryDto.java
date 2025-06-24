package org.example.shareddtos.dto;
import lombok.*;

/**
 * DTO used to transfer summary information of an AdoptionPost.
 * used in list views.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AdoptionPostSummaryDto {
    private Long id;
    private String name;
    private String species;
    private String breed;
    private Integer age;// in months
    private String gender;
    private String color;
    private String location;
    private Boolean active;
}