package org.example.shareddtos.dto;
import lombok.*;

/**
 * DTO used to transfer summary information of an AdoptionPost and userId who owns saved search via RabbitMQ
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AdoptionPostRabbitMQDto {
    private Long userId;
    private String name;
    private String species;
    private String breed;
    private Integer age;// in months
    private String gender;
    private String color;
    private String location;
}