package com.example.authservice.service;

import com.example.authservice.dto.UserProfileDTO;
import com.example.authservice.model.UserProfile;
import com.example.authservice.repository.UserProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.example.shareddtos.dto.EmailResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;

@Service
public class ProfileService {
    private final UserProfileRepository repository;

    public ProfileService(UserProfileRepository userProfileRepository) {
        this.repository = userProfileRepository;
    }

    @Transactional
    public void update(UserProfileDTO updateRequest, Long userId) {
        UserProfile userProfile = repository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato"));
        if (updateRequest.getName() != null) {userProfile.setName(updateRequest.getName());}
        if (updateRequest.getSurname() != null) {userProfile.setSurname(updateRequest.getSurname());}
        if (updateRequest.getProfilePicture() != null) {userProfile.setProfilePicture(updateRequest.getProfilePicture());}

        repository.save(userProfile);
    }


    public UserProfileDTO getUserById(Long userId) {
        UserProfile profile = repository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Not found" + userId));
        return toUserProfileDto(profile);
    }

    public EmailResponseDto getEmailById(Long userId) {
        UserProfile profile = repository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Not found" + userId));
        return toEmailResponseDto(profile);
    }

    private UserProfileDTO toUserProfileDto(UserProfile profile) {
        UserProfileDTO dto = new  UserProfileDTO();
        dto.setId(profile.getId());
        dto.setName(profile.getName());
        dto.setSurname(profile.getSurname());
        dto.setProfilePicture(profile.getProfilePicture());
        return dto;
    }

    private EmailResponseDto toEmailResponseDto(UserProfile profile) {
        EmailResponseDto dto = new EmailResponseDto();
        dto.setEmail(profile.getEmail());
        dto.setName(profile.getName());
        dto.setSurname(profile.getSurname());
        return dto;


    }


}
