package org.example.adoptionpostservice.repository;

import org.example.adoptionpostservice.model.AdoptionPost;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

import java.util.ArrayList;
import java.util.List;
/**
 * Specification class to build dynamic filters for AdoptionPost entities.
 */
public class AdoptionPostSpecification {

    /**
     * Builds a Specification with optional filters for species, breed, gender, age, and color.
     *
     * @param speciesList list of species to filter
     * @param breedList   list of breeds to filter
     * @param gender      gender to filter
     * @param minAge      minimum age to filter
     * @param maxAge      maximum age to filter
     * @param colorList   list of colors to filter
     * @return a Specification to be used with the repository
     */
    public static Specification<AdoptionPost> withFilters(
            List<String> speciesList,
            List<String> breedList,
            String gender,
            Integer minAge,
            Integer maxAge,
            List<String> colorList,
            Boolean activeOnly
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            // Filtering by species
            if (speciesList != null && !speciesList.isEmpty()) {
                predicates.add(root.get("species").in(speciesList));
            }
            // Filtering by breed
            if (breedList != null && !breedList.isEmpty()) {
                predicates.add(root.get("breed").in(breedList));
            }
            // Filtering by color
            if (colorList != null && !colorList.isEmpty()) {
                predicates.add(root.get("color").in(colorList));
            }
            // Filtering by gender
            if (gender != null && !gender.isBlank()) {
                predicates.add(cb.equal(root.get("gender"), gender));
            }
            // Filtering by minimum age
            if (minAge != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("age"), minAge));
            }
            // Filtering by maximum age
            if (maxAge != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("age"), maxAge));
            }
            //Filtering only active posts (not adopted)
            if (activeOnly != null && activeOnly) {
                predicates.add(cb.isTrue(root.get("active")));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}