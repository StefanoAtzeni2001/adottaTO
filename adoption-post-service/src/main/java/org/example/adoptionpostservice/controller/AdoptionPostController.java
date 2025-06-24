package org.example.adoptionpostservice.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.example.adoptionpostservice.dto.AdoptionPostDetailDto;
import org.example.shareddtos.dto.AdoptionPostSearchDto;
import org.example.shareddtos.dto.AdoptionPostSummaryDto;
import org.example.adoptionpostservice.service.AdoptionPostService;
import static org.example.adoptionpostservice.constants.AdoptionPostEndPoints.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Base64;
import java.util.NoSuchElementException;

/**
 * REST Controller responsible for managing pet adoption posts.
 * It provides functionality for:
 * - Retrieving all adoption posts with pagination
 * - Filtering adoption posts by various criteria
 * - Getting detailed information about specific adoption posts
 * - Creating new adoption posts
 * - Updating existing adoption posts
 * - Deleting adoption posts with user authorization
 */
@RestController
@RequestMapping("/adoption")
public class AdoptionPostController {

    private final AdoptionPostService adoptionPostService;

    /**
     * Constructor with dependencies injected.
     *
     * @param service the service for managing adoption post operations
     */
    public AdoptionPostController(AdoptionPostService service) {
        this.adoptionPostService = service;
    }

    /**
     * Retrieves adoption posts filtered by specified criteria with pagination.
     *
     * @param filterDto the filter criteria for searching adoption posts
     * @param pageable pagination information for the results
     * @return a page of adoption post summaries matching the filter criteria
     */
    @GetMapping(GET_FILTERED_ADOPTION_POSTS)
    public Page<AdoptionPostSummaryDto> getAdoptionPostsFilteredBy(
            @Valid AdoptionPostSearchDto filterDto,
            Pageable pageable) {
        return adoptionPostService.getFilteredPosts(filterDto, pageable);
    }

    /**
     * Retrieves detailed information about a specific adoption post by its ID.
     *
     * @param postId [from path] the ID of the adoption post to retrieve
     * @return ResponseEntity containing the adoption post details or 404 if not found
     */
    @GetMapping(GET_ADOPTION_POST_BY_ID)
    public ResponseEntity<AdoptionPostDetailDto> getAdoptionPostById(@PathVariable Long postId) {
        try {
            AdoptionPostDetailDto dto = adoptionPostService.getPostById(postId);
            return ResponseEntity.ok(dto);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Creates a new adoption post for the specified user.
     *
     * @param postDto the adoption post data to create
     * @param imageFile optional image to attach to the post (multipart part)
     * @param userId [from header] the ID of the user creating the post
     * @return ResponseEntity containing the created adoption post with HTTP 201 status,
     *         or HTTP 500 if image processing fails
     */
    @PostMapping(value = CREATE_ADOPTION_POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdoptionPostDetailDto> createAdoptionPost(
            @RequestPart("post") @Valid AdoptionPostDetailDto postDto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile,
            @RequestHeader("User-Id") Long userId) {
        try {
            String base64Image = null;
            if (imageFile != null && !imageFile.isEmpty()) {
                base64Image = Base64.getEncoder().encodeToString(imageFile.getBytes());
            }
            postDto.setImageBase64(base64Image);
            AdoptionPostDetailDto created = adoptionPostService.createPost(postDto, userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Deletes an adoption post by its ID with user authorization check.
     *
     * @param postId [from path]the ID of the adoption post to delete
     * @param userId [from header] the ID of the user requesting the deletion
     * @return ResponseEntity with HTTP 204 (No Content) on success,
     *         HTTP 403 (Forbidden) if unauthorized, or HTTP 404 if not found
     */
    @DeleteMapping(DELETE_ADOPTION_POST_BY_ID)
    public ResponseEntity<Void> deleteAdoptionPost(
            @PathVariable Long postId,
            @RequestHeader("User-Id") Long userId) {
        try {
            adoptionPostService.deletePost(postId, userId);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Updates an existing adoption post with new data after verifying user authorization.
     *
     * @param postDto the updated adoption post data
     * @param postId [from path] the ID of the adoption post to update
     * @param userId [from header] the ID of the user requesting the update
     * @return ResponseEntity containing the updated adoption post,
     *         HTTP 403 if unauthorized, or other appropriate status codes
     */
    @PutMapping(UPDATE_ADOPTION_POST_BY_ID)
    public ResponseEntity<AdoptionPostDetailDto> updateAdoptionPost(
            @RequestBody AdoptionPostDetailDto postDto,
            @PathVariable Long postId,
            @RequestHeader("User-Id") Long userId) {
        try {
            AdoptionPostDetailDto updated = adoptionPostService.updatePost(postDto, postId, userId);
            return ResponseEntity.ok(updated);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Retrieves adoption posts created by the specified user.
     *
     * @param userId [from header] the ID of the user requesting his own created posts
     * @param pageable pagination information for the results
     * @return a page of adoption post summaries created by the user
     */
    @GetMapping(GET_ADOPTION_POSTS_BY_OWNER)
    public Page<AdoptionPostSummaryDto> getAdoptionPostsByOwner(
            @RequestHeader("User-Id") Long userId,
            Pageable pageable) {
        return adoptionPostService.getPostsByOwnerId(userId, pageable);
    }

    /**
     * Retrieves adoption posts adopted by the specified user.
     *
     * @param userId [from header] the ID of the user requesting his own adopted posts
     * @param pageable pagination information for the results
     * @return a page of adoption post summaries adopted by the user
     */
    @GetMapping(GET_ADOPTION_POSTS_BY_ADOPTER)
    public Page<AdoptionPostSummaryDto> getAdoptionPostsByAdopter(
            @RequestHeader("User-Id") Long userId,
            Pageable pageable) {
        return adoptionPostService.getPostsByAdopterId(userId, pageable);
    }
}