package org.example.adoptionpostservice.repository;


import org.example.adoptionpostservice.model.AdoptionPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AdoptionPostRepository extends JpaRepository<AdoptionPost, Long>,
        JpaSpecificationExecutor<AdoptionPost> {
}