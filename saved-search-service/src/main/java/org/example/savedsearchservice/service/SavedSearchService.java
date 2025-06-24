package org.example.savedsearchservice.service;


import jakarta.persistence.EntityNotFoundException;
import org.example.savedsearchservice.model.SavedSearch;
import org.example.savedsearchservice.repository.SavedSearchRepository;
import org.example.shareddtos.dto.AdoptionPostSavedSearchDto;
import org.example.shareddtos.dto.AdoptionPostSearchDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class responsible for managing user's saved search filters.
 * Provides functionality to:
 * - Save a new search
 * - Delete an existing saved search
 * - Retrieve all saved searches for a user
 */
@Service
public class SavedSearchService {

    private final SavedSearchRepository repository;

    /**
     * Constructor for dependency injection.
     *
     * @param repository the repository handling persistence of saved searches
     */
    public SavedSearchService(SavedSearchRepository repository) {
        this.repository = repository;
    }

    /**
     * Persists a new saved search filter for the given user.
     *
     * @param userId the ID of the user who owns the search
     * @param search the search criteria to save
     * @return the saved search filter as a DTO
     */
    public AdoptionPostSearchDto saveSearch(Long userId, AdoptionPostSearchDto search) {
        SavedSearch savedSearch = SavedSearch.builder()
                .userId(userId)
                .species(search.getSpecies())
                .breed(search.getBreed())
                .gender(search.getGender())
                .minAge(search.getMinAge())
                .maxAge(search.getMaxAge())
                .color(search.getColor())
                .location(search.getLocation())
                .build();
        SavedSearch saved = repository.save(savedSearch);
        return toSearchDto(saved);
    }

    /**
     * Deletes a saved search
     *
     * @param searchId the ID of the search to delete
     * @param userId   the ID of the user requesting the deletion
     * @throws AccessDeniedException if the user does not own the search
     * @throws EntityNotFoundException if the search does not exist
     */
    @Transactional
    public void deleteSearch(Long searchId, Long userId) throws AccessDeniedException {
        SavedSearch savedSearch = repository.findById(searchId)
                .orElseThrow(() -> new EntityNotFoundException("Saved search not found with id " + searchId));

        if (!savedSearch.getUserId().equals(userId)) {
            throw new AccessDeniedException("You are not the owner of this saved search");
        }

        repository.delete(savedSearch);
    }

    /**
     * Retrieves all saved search filters for a given user.
     *
     * @param userId the ID of the user
     * @return list of saved searches in DTO format
     */
    public List<AdoptionPostSavedSearchDto> getSavedSearchesByUser(Long userId) {
        return repository.findByUserId(userId).stream()
                .map(this::toSavedSearchDto)
                .collect(Collectors.toList());
    }


    /**
     * Converts a SavedSearch entity to its corresponding DTO representation.
     *
     * @param savedSearch the entity to convert
     * @return the DTO
     */
    //--------------------------------------------------------------TODO: da implementare con un mapper automatico
    private AdoptionPostSearchDto toSearchDto(SavedSearch savedSearch) {
        AdoptionPostSearchDto dto = new AdoptionPostSearchDto();
        dto.setSpecies(savedSearch.getSpecies());
        dto.setBreed(savedSearch.getBreed());
        dto.setGender(savedSearch.getGender());
        dto.setMinAge(savedSearch.getMinAge());
        dto.setMaxAge(savedSearch.getMaxAge());
        dto.setColor(savedSearch.getColor());
        dto.setLocation(savedSearch.getLocation());

        return dto;
    }

    private AdoptionPostSavedSearchDto toSavedSearchDto(SavedSearch savedSearch) {
        AdoptionPostSavedSearchDto dto = new AdoptionPostSavedSearchDto();
        dto.setId(savedSearch.getId());
        dto.setSpecies(savedSearch.getSpecies());
        dto.setBreed(savedSearch.getBreed());
        dto.setGender(savedSearch.getGender());
        dto.setMinAge(savedSearch.getMinAge());
        dto.setMaxAge(savedSearch.getMaxAge());
        dto.setColor(savedSearch.getColor());
        dto.setLocation(savedSearch.getLocation());
        return dto;
    }
}
