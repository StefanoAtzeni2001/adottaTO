package org.example.adoptionpostservice.service;

import org.example.adoptionpostservice.dto.AdoptionPostDetailDto;
import org.example.adoptionpostservice.dto.AdoptionPostListDto;
import org.example.adoptionpostservice.model.AdoptionPost;
import org.example.adoptionpostservice.repository.AdoptionPostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class AdoptionPostService {
    @Autowired
    private final AdoptionPostRepository repository;

    public AdoptionPostService(AdoptionPostRepository repository) {
        this.repository = repository;
    }
    //Get all post
    public Page<AdoptionPostListDto> getAllPosts(Pageable pageable) {
        return repository.findAll(pageable)
                .map(this::toListDto);
    }
    //Get a post by id
    public AdoptionPostDetailDto getPostById(Long id) {
        AdoptionPost post = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Not found" + id));
        return toDetailDto(post);
    }

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
    private AdoptionPostListDto toListDto(AdoptionPost post) {
        AdoptionPostListDto dto = new AdoptionPostListDto();
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