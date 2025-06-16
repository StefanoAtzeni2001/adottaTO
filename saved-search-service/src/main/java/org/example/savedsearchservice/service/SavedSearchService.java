package org.example.savedsearchservice.service;


import jakarta.persistence.EntityNotFoundException;
import org.example.savedsearchservice.model.SavedSearch;
import org.example.savedsearchservice.repository.SavedSearchRepository;
import org.example.shareddtos.dto.AdoptionPostSearchDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SavedSearchService {
    private final SavedSearchRepository repository;

    public SavedSearchService(SavedSearchRepository repository) {
        this.repository = repository;
    }


    public AdoptionPostSearchDto saveSearch(Long userId, AdoptionPostSearchDto search) {
        SavedSearch savedSearch = SavedSearch.builder()
                .userId(userId)
                .species(search.getSpecies())
                .breed(search.getBreed())
                .gender(search.getGender())
                .minAge(search.getMinAge())
                .maxAge(search.getMaxAge())
                .color(search.getColor())
                .build();
        SavedSearch saved = repository.save(savedSearch);
        return toSearchDto(saved);
    }

    @Transactional
    public void deleteSearch(Long searchId, Long userId) throws AccessDeniedException {
        SavedSearch savedSearch = repository.findById(searchId)
                .orElseThrow(() -> new EntityNotFoundException("Saved search not found with id " + searchId));

        if (!savedSearch.getUserId().equals(userId)) {
            throw new AccessDeniedException("You are not the owner of this saved search");
        }

        repository.delete(savedSearch);
    }

    public List<AdoptionPostSearchDto> getSavedSearchesByUser(Long userId) {
        return repository.findByUserId(userId).stream()
                .map(this::toSearchDto)
                .collect(Collectors.toList());
    }

    //--------------------------------------------------------------TODO: da implementare con un mapper automatico
    private AdoptionPostSearchDto toSearchDto(SavedSearch savedSearch) {
        AdoptionPostSearchDto dto = new AdoptionPostSearchDto();
        dto.setSpecies(savedSearch.getSpecies());
        dto.setBreed(savedSearch.getBreed());
        dto.setGender(savedSearch.getGender());
        dto.setMinAge(savedSearch.getMinAge());
        dto.setMaxAge(savedSearch.getMaxAge());
        dto.setColor(savedSearch.getColor());

        return dto;
    }
}
