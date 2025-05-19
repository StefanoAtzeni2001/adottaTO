package org.example.adoptionpostservice.dto;

import lombok.Data;
import java.util.List;
//Dto used to request an AdoptionPost list with filtering
@Data
public class AdoptionPostFilterRequestDto {
    private List<String> species;
    private List<String> breed;
    private String gender;
    private Integer minAge;
    private Integer maxAge;
    private List<String> color;
}