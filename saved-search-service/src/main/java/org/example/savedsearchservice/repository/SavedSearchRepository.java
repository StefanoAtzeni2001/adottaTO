package org.example.savedsearchservice.repository;

import org.example.savedsearchservice.model.SavedSearch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SavedSearchRepository extends JpaRepository<SavedSearch, Long> {
    List<SavedSearch> findByUserId(Long userId);
    @Query("SELECT s.userId FROM SavedSearch s " +
            "LEFT JOIN s.species sp " +
            "LEFT JOIN s.breed br " +
            "LEFT JOIN s.color col " +
            "WHERE (:species IS NULL OR sp = :species OR s.species IS EMPTY) " +
            "AND (:breed IS NULL OR br = :breed OR s.breed IS EMPTY) " +
            "AND (s.gender IS NULL OR LOWER(s.gender) = LOWER(:gender)) " +
            "AND (s.minAge IS NULL OR s.minAge <= :age) " +
            "AND (s.maxAge IS NULL OR s.maxAge >= :age) " +
            "AND (:color IS NULL OR col = :color OR s.color IS EMPTY) ")
    List<Long> findMatchingUserIds(@Param("species") String species,
                                   @Param("breed") String breed,
                                   @Param("gender") String gender,
                                   @Param("age") Integer age,
                                   @Param("color") String color);
}