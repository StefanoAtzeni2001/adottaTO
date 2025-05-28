package org.example.adoptionpostservice.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.example.adoptionpostservice.dto.AdoptionPostDetailDto;
import org.example.adoptionpostservice.dto.AdoptionPostFilterRequestDto;
import org.example.adoptionpostservice.dto.AdoptionPostSummaryDto;
import org.example.adoptionpostservice.model.AdoptionPost;
import org.example.adoptionpostservice.repository.AdoptionPostRepository;
import org.example.adoptionpostservice.repository.AdoptionPostSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
public class AdoptionPostService {
    @Autowired
    private final AdoptionPostRepository repository;

    public AdoptionPostService(AdoptionPostRepository repository) {
        this.repository = repository;
    }
    //Get all post
    public Page<AdoptionPostSummaryDto> getAllPosts(Pageable pageable) {
        return repository.findAll(pageable)
                .map(this::toSummaryDto);
    }
    //Get a post by id
    public AdoptionPostDetailDto getPostById(Long id) {
        AdoptionPost post = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Not found" + id));
        return toDetailDto(post);
    }
    //Get a list of filtered post
    public Page<AdoptionPostSummaryDto> getFilteredPosts(@Valid AdoptionPostFilterRequestDto filterDto, Pageable pageable) {
        Specification<AdoptionPost> spec = AdoptionPostSpecification.withFilters(
                filterDto.getSpecies(),
                filterDto.getBreed(),
                filterDto.getGender(),
                filterDto.getMinAge(),
                filterDto.getMaxAge(),
                filterDto.getColor()
        );
        return repository.findAll(spec, pageable)
                .map(this::toSummaryDto);
    }
    //Create and save a new post
    public AdoptionPostDetailDto createPost(AdoptionPostDetailDto dto,Long userId) {
        AdoptionPost post = AdoptionPost.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .species(dto.getSpecies())
                .breed(dto.getBreed())
                .gender(dto.getGender())
                .age(dto.getAge())
                .color(dto.getColor())
                .ownerId(userId)
                .publicationDate(LocalDateTime.now())
                .build();
        AdoptionPost saved = repository.save(post);
        return toDetailDto(saved);
    }

    //Delete a post if user is owner
    @Transactional
    public void deletePost(Long postId, Long userId) throws AccessDeniedException {
        AdoptionPost post = repository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id " + postId));
        //check if user is owner
        if (!post.getOwnerId().equals(userId)) {
            throw new AccessDeniedException("You are not the owner of this post");
        }

        repository.delete(post);
    }

    //Update a post if user is owner
    public AdoptionPostDetailDto updatePost(AdoptionPostDetailDto dto,Long postId, Long userId) throws AccessDeniedException {
        AdoptionPost post = repository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id " + postId));
        //check if user is owner
        if (!post.getOwnerId().equals(userId)) {
            throw new AccessDeniedException("You are not the owner of this post");
        }
        if (dto.getName() != null) post.setName(dto.getName());
        if (dto.getDescription() != null) post.setDescription(dto.getDescription());
        if (dto.getSpecies() != null) post.setSpecies(dto.getSpecies());
        if (dto.getBreed() != null) post.setBreed(dto.getBreed());
        if (dto.getGender() != null) post.setGender(dto.getGender());
        if (dto.getAge() != null) post.setAge(dto.getAge());
        if (dto.getColor() != null) post.setColor(dto.getColor());

        AdoptionPost updated = repository.save(post);
        return toDetailDto(updated);
    }
    //---------------------------Mapping dto

    //Mapping from AdoptionPost to AdoptionPostDetailDto
    private AdoptionPostDetailDto toDetailDto(AdoptionPost post) {
        AdoptionPostDetailDto dto = new AdoptionPostDetailDto();
        dto.setId(post.getId());
        dto.setName(post.getName());
        dto.setDescription(post.getDescription());
        dto.setSpecies(post.getSpecies());
        dto.setBreed(post.getBreed());
        dto.setGender(post.getGender());
        dto.setAge(post.getAge());
        dto.setColor(post.getColor());
        dto.setPublicationDate(post.getPublicationDate());
        dto.setOwnerId(post.getOwnerId());
        return dto;
    }
    //Mapping from AdoptionPost to AdoptionPostListDto
    private AdoptionPostSummaryDto toSummaryDto(AdoptionPost post) {
        AdoptionPostSummaryDto dto = new AdoptionPostSummaryDto();
        dto.setId(post.getId());
        dto.setName(post.getName());
        dto.setSpecies(post.getSpecies());
        dto.setBreed(post.getBreed());
        dto.setGender(post.getGender());
        dto.setAge(post.getAge());
        dto.setColor(post.getColor());
        return dto;
    }


}