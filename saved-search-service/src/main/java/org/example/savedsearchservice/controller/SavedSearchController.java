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

import static org.example.savedsearchservice.constants.SavedSearchEndPoints.*;

/**
 * REST controller that handles operations related to user's saved search criteria.
 * Provides endpoints to:
 * - Save a new search
 * - Delete a saved search
 * - Retrieve all saved searches of a user
 */
@RestController
@RequestMapping()
public class SavedSearchController {

    private final SavedSearchService savedSearchService;

    /**
     * Constructor injection for the saved search service.
     *
     * @param service the service handling business logic for saved searches
     */
    public SavedSearchController(SavedSearchService service) {
        this.savedSearchService = service;
    }

    /**
     * Saves a new search filter for the current user.
     *
     * @param searchDto the search filter to be saved
     * @param userId the ID of the authenticated user
     * @return the created search filter with status 201 (Created)
     */
    @PostMapping(SAVE_SEARCH)
    public ResponseEntity<?> saveSearch(@RequestBody AdoptionPostSearchDto searchDto, @RequestHeader("User-Id") Long userId) {
        AdoptionPostSearchDto created = savedSearchService.saveSearch(userId, searchDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Deletes a saved search by ID
     *
     * @param searchId ID of the saved search to delete
     * @param userId ID of the user attempting deletion
     * @return 204 No Content on success, 403 Forbidden if not the owner, 404 Not Found if not existing
     */
    @DeleteMapping(DELETE_SAVED_SEARCH)
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

    /**
     * Retrieves all saved searches for the authenticated user.
     *
     * @param userId the ID of the user making the request
     * @return list of the user's saved searches with status 200 OK
     */
    @GetMapping(GET_MY_SAVED_SEARCHES)
    public ResponseEntity<List<AdoptionPostSavedSearchDto>> getMySavedSearches(@RequestHeader("User-Id") Long userId) {
        List<AdoptionPostSavedSearchDto> searches = savedSearchService.getSavedSearchesByUser(userId);
        return ResponseEntity.ok(searches);
    }

}
