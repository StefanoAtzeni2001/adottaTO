package org.example.adoptionpostservice.repository;

import org.example.adoptionpostservice.model.AdoptionPost;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;

//Specification class to build dynamic queries for filtering AdoptionPost entities.

public class AdoptionPostSpecification {

    public static Specification<AdoptionPost> withFilters(
            List<String> speciesList,
            List<String> breedList,
            String gender,
            Integer minAge,
            Integer maxAge,
            List<String> colorList
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            //filtering by multiple species
            if (speciesList != null && !speciesList.isEmpty()) {
                predicates.add(root.get("species").in(speciesList));
            }
            //filtering by multiple breeds
            if (breedList != null && !breedList.isEmpty()) {
                predicates.add(root.get("breed").in(breedList));
            }
            //filtering by multiple breeds
            if (colorList != null && !colorList.isEmpty()) {
                predicates.add(root.get("color").in(colorList));
            }
            //filtering by gender
            if (gender != null && !gender.isBlank()) {
                predicates.add(cb.equal(root.get("gender"), gender));
            }
            //filtering by minAge
            if (minAge != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("age"), minAge));
            }
            //filtering by maxAge
            if (maxAge != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("age"), maxAge));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}