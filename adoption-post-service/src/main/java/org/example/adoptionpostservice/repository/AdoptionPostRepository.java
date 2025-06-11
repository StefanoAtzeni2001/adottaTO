package org.example.adoptionpostservice.repository;


import org.example.adoptionpostservice.model.AdoptionPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Repository interface for AdoptionPost entities.
 * Supports CRUD operations and specification-based filtering.
 */
public interface AdoptionPostRepository extends JpaRepository<AdoptionPost, Long>,
        JpaSpecificationExecutor<AdoptionPost> {

    Page<AdoptionPost> findByOwnerId(Long ownerId, Pageable pageable);
    Page<AdoptionPost> findByAdopterId(Long adopterId, Pageable pageable);
}