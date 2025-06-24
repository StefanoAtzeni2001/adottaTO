package org.example.savedsearchservice.controller;

import jakarta.persistence.EntityNotFoundException;
import org.example.savedsearchservice.service.SavedSearchService;
import org.example.shareddtos.dto.AdoptionPostSavedSearchDto;
import org.example.shareddtos.dto.AdoptionPostSearchDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequestMapping()
public class SavedSearchController {

    private final SavedSearchService savedSearchService;

    public SavedSearchController(SavedSearchService service) {
        this.savedSearchService = service;
    }
    @PostMapping("/save-search")
    public ResponseEntity<?> saveSearch(@RequestBody AdoptionPostSearchDto searchDto, @RequestHeader("User-Id") Long userId) {
        AdoptionPostSearchDto created = savedSearchService.saveSearch(userId, searchDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("/delete-saved-search/{searchId}")
    public ResponseEntity<Void> deleteSavedSearch(@PathVariable Long searchId, @RequestHeader("User-Id") Long userId) {
        try {
            savedSearchService.deleteSearch(searchId, userId);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/get-my-saved-search")
    public ResponseEntity<List<AdoptionPostSavedSearchDto>> getMySavedSearches(@RequestHeader("User-Id") Long userId) {
        List<AdoptionPostSavedSearchDto> searches = savedSearchService.getSavedSearchesByUser(userId);
        return ResponseEntity.ok(searches);
    }

}
