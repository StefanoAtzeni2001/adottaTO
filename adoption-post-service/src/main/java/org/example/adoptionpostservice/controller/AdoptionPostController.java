package org.example.adoptionpostservice.controller;


import org.example.adoptionpostservice.dto.AdoptionPostDetailDto;
import org.example.adoptionpostservice.dto.AdoptionPostListDto;
import org.example.adoptionpostservice.service.AdoptionPostService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/adoptions")
public class AdoptionPostController {

    private final AdoptionPostService adoptionPostService;

    public AdoptionPostController(AdoptionPostService service) {
        this.adoptionPostService = service;
    }

    @GetMapping("/get-list")
    public Page<AdoptionPostListDto> getAllAdoptionPosts(Pageable pageable) {
        return adoptionPostService.getAllPosts(pageable);
    }

    @GetMapping("get-by-id/{id}")
    public ResponseEntity<AdoptionPostDetailDto> getAdoptionPostById(@PathVariable Long id) {
        try {
            AdoptionPostDetailDto dto = adoptionPostService.getPostById(id);
            return ResponseEntity.ok(dto);
        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
    }
}