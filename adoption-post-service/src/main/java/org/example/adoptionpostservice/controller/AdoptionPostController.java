package org.example.adoptionpostservice.controller;


import jakarta.validation.Valid;
import org.example.adoptionpostservice.dto.AdoptionPostDetailDto;
import org.example.adoptionpostservice.dto.AdoptionPostFilterRequestDto;
import org.example.adoptionpostservice.dto.AdoptionPostSummaryDto;
import org.example.adoptionpostservice.service.AdoptionPostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;

@RestController
@RequestMapping()
public class AdoptionPostController {

    private final AdoptionPostService adoptionPostService;
    public AdoptionPostController(AdoptionPostService service) {
        this.adoptionPostService = service;
    }

    //per testing
    @GetMapping("/get-list")
    public Page<AdoptionPostSummaryDto> getAllAdoptionPosts(Pageable pageable) {
        return adoptionPostService.getAllPosts(pageable);
    }

    @GetMapping("/get-list-filtered")
    public Page<AdoptionPostSummaryDto> getAdoptionPostsFilteredBy(@Valid AdoptionPostFilterRequestDto filterDto, Pageable pageable) {
        return adoptionPostService.getFilteredPosts(filterDto,pageable);

    }
    @GetMapping("get-by-id/{postId}")
    public ResponseEntity<AdoptionPostDetailDto> getAdoptionPostById(@PathVariable Long postId) {
        try {
            AdoptionPostDetailDto dto = adoptionPostService.getPostById(postId);
            return ResponseEntity.ok(dto);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("create-adoption-post")
    public ResponseEntity<AdoptionPostDetailDto> createAdoptionPost(@Valid @RequestBody AdoptionPostDetailDto postDto, @RequestHeader("User-Id") Long userId) {
        AdoptionPostDetailDto created = adoptionPostService.createPost(postDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @DeleteMapping("delete-by-id/{postId}")
    public ResponseEntity<Void> deleteAdoptionPost(@PathVariable Long postId, @RequestHeader("User-Id") Long userId)  {
        try {
            adoptionPostService.deletePost(postId, userId);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PutMapping("update-by-id/{postId}")
    public ResponseEntity<AdoptionPostDetailDto> updateAdoptionPost(@RequestBody AdoptionPostDetailDto postDto, @PathVariable Long postId, @RequestHeader("User-Id") Long userId){
        try {
            AdoptionPostDetailDto updated = adoptionPostService.updatePost(postDto, postId, userId);
            return ResponseEntity.ok(updated);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }
}

