package org.example.adoptionpostservice.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.example.adoptionpostservice.model.AdoptionPost;
import org.example.adoptionpostservice.repository.AdoptionPostRepository;
import org.example.adoptionpostservice.repository.AdoptionPostSpecification;
import org.example.shareddtos.dto.AdoptionPostDetailDto;
import org.example.shareddtos.dto.AdoptionPostSearchDto;
import org.example.shareddtos.dto.AdoptionPostSummaryDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

/**
 * Service for managing adoption posts.
 */
@Service
public class AdoptionPostService {

    private final AdoptionPostRepository repository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${app.rabbitmq.exchange}")
    private String adottatoExchange;
    @Value("${app.rabbitmq.routingkey.new-post}")
    private String newPostRoutingKey;

    /**
     * Constructor
     *
     * @param repository the adoption post repository
     */
    public AdoptionPostService(AdoptionPostRepository repository, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate ;
    }

    /**
     * Retrieves a post by its ID.
     *
     * @param id post ID
     * @return detailed AdoptionPost DTO
     * @throws NoSuchElementException if post not found
     */
    public AdoptionPostDetailDto getPostById(Long id) {
        AdoptionPost post = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Not found" + id));
        return toDetailDto(post);
    }

    /**
     * Retrieves filtered adoption posts with pagination.
     *
     * @param filterDto filtering criteria
     * @param pageable  pagination information
     * @return paginated list of filtered AdoptionPost summaries
     */
    public Page<AdoptionPostSummaryDto> getFilteredPosts(@Valid AdoptionPostSearchDto filterDto, Pageable pageable) {
        // Build a dynamic Specification based on provided filters
        Specification<AdoptionPost> spec = AdoptionPostSpecification.withFilters(
                filterDto.getSpecies(),
                filterDto.getBreed(),
                filterDto.getGender(),
                filterDto.getMinAge(),
                filterDto.getMaxAge(),
                filterDto.getColor(),
                filterDto.getActiveOnly()

        );
        // Execute the query with filters and pagination, then map results to summary DTOs
        return repository.findAll(spec, pageable)
                .map(this::toSummaryDto);
    }

    /**
     * Creates and saves a new post.
     *
     * @param dto    AdoptionPost details
     * @param userId Requesting user ID (=owner)
     * @return saved AdoptionPost DTO
     */
    public AdoptionPostDetailDto createPost(AdoptionPostDetailDto dto, Long userId) {
        AdoptionPost post = AdoptionPost.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .species(dto.getSpecies())
                .breed(dto.getBreed())
                .gender(dto.getGender())
                .age(dto.getAge())
                .color(dto.getColor())
                .location(dto.getLocation())
                .ownerId(userId)
                .active(true)
                .adopterId(null)
                .publicationDate(LocalDateTime.now())
                .build();
        AdoptionPost saved = repository.save(post); //saving in db
        sendNewPostEvent(toSummaryDto(post)); //sending message with rabbitMQ
        return toDetailDto(saved);
    }

    /**
     * Deletes a post if the user is the owner.
     *
     * @param postId AdoptionPost ID
     * @param userId Requesting user ID
     * @throws AccessDeniedException if user is not the owner
     */
    @Transactional
    public void deletePost(Long postId, Long userId) throws AccessDeniedException {
        AdoptionPost post = repository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id " + postId));
        if (!post.getOwnerId().equals(userId)) {
            throw new AccessDeniedException("You are not the owner of this post");
        }
        repository.delete(post);
    }

    /**
     * Updates a post if the user is the owner.
     *
     * @param dto    updated post data
     * @param postId AdoptionPost ID
     * @param userId Requesting user ID
     * @return updated AdoptionPost DTO
     * @throws AccessDeniedException if user is not the owner
     */
    public AdoptionPostDetailDto updatePost(AdoptionPostDetailDto dto, Long postId, Long userId) throws AccessDeniedException {
        AdoptionPost post = repository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException("Post not found with id " + postId));
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
        if (dto.getLocation() != null) post.setLocation(dto.getLocation());

        AdoptionPost updated = repository.save(post);
        return toDetailDto(updated);
    }

    /**
     * Returns a paginated list of adoption posts owned by the given owner.
     * Checks that the requesting user matches the ownerId.
     *
     * @param ownerId owner ID
     * @param pageable pagination information
     * @return a page of AdoptionPostSummaryDto
     */
    public Page<AdoptionPostSummaryDto> getPostsByOwnerId(Long ownerId, Pageable pageable) {
        return repository.findByOwnerId(ownerId, pageable)
                .map(this::toSummaryDto);
    }

    /**
     * Returns a paginated list of adoption posts adopted by the given adopter.
     * Checks that the requesting user matches the adopterId.
     *
     * @param adopterId adopter ID
     * @param pageable pagination information
     * @return a page of AdoptionPostSummaryDto
     */
    public Page<AdoptionPostSummaryDto> getPostsByAdopterId(Long adopterId, Pageable pageable)  {
        return repository.findByAdopterId(adopterId, pageable)
                .map(this::toSummaryDto);
    }


    public void sendNewPostEvent(AdoptionPostSummaryDto dto) {
        rabbitTemplate.convertAndSend(adottatoExchange, newPostRoutingKey, dto);
    }


//--------------------------------------------------------------TODO: da implementare con un mapper automatico
    /**
     * Converts a AdoptionPost entity to detailed DTO.
     *
     * @param post the AdoptionPost entity
     * @return AdoptionPost detailed DTO
     */
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
        dto.setActive(post.getActive());
        dto.setAdopterId(post.getAdopterId());

        return dto;
    }

    /**
     * Converts a post entity to summary DTO.
     *
     * @param post the AdoptionPost entity
     * @return AdoptionPost summary DTO
     */
    private AdoptionPostSummaryDto toSummaryDto(AdoptionPost post) {
        AdoptionPostSummaryDto dto = new AdoptionPostSummaryDto();
        dto.setId(post.getId());
        dto.setName(post.getName());
        dto.setSpecies(post.getSpecies());
        dto.setBreed(post.getBreed());
        dto.setGender(post.getGender());
        dto.setAge(post.getAge());
        dto.setColor(post.getColor());
        dto.setActive(post.getActive());
        return dto;
    }
}
