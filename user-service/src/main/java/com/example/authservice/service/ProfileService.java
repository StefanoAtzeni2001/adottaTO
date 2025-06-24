package com.example.authservice.service;

import com.example.authservice.dto.UserProfileDTO;
import com.example.authservice.model.UserProfile;
import com.example.authservice.repository.UserProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProfileService {
    private final UserProfileRepository userProfileRepository;

    public ProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    public void update(UserProfileDTO updateRequest, Long userId) {
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Utente non trovato"));

        if (updateRequest.getName() != null) {
            userProfile.setName(updateRequest.getName());
        }
        if (updateRequest.getSurname() != null) {
            userProfile.setSurname(updateRequest.getSurname());
        }

        if (updateRequest.getProfilePicture() != null) {
            userProfile.setProfilePicture(updateRequest.getProfilePicture());
        }

        userProfileRepository.save(userProfile);
    }

}
