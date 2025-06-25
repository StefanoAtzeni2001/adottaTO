package com.example.authservice.service;

import com.example.authservice.dto.UserProfileDTO;
import com.example.authservice.model.UserProfile;
import com.example.authservice.repository.UserProfileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.example.shareddtos.dto.EmailResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

/**
 * Service responsible for handling user profile operations such as retrieval and update.
 */
@Service
public class ProfileService {

    private final UserProfileRepository repository;

    /**
     * Constructs a new ProfileService with the given UserProfileRepository.
     *
     * @param userProfileRepository the repository used for accessing user profile data
     */
    public ProfileService(UserProfileRepository userProfileRepository) {
        this.repository = userProfileRepository;
    }

    /**
     * Updates the profile information of a user.
     *
     * @param updateRequest the updated profile data
     * @param userId the ID of the user whose profile is to be updated
     * @throws EntityNotFoundException if the user is not found
     */
    @Transactional
    public void update(UserProfileDTO updateRequest, Long userId) {
        UserProfile userProfile = repository.findById(userId)
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

        repository.save(userProfile);
    }

    /**
     * Retrieves the profile data of a user by their ID.
     *
     * @param userId the ID of the user
     * @return a UserProfileDTO containing the user's profile data
     * @throws NoSuchElementException if the user is not found
     */
    public UserProfileDTO getUserById(Long userId) {
        UserProfile profile = repository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Not found " + userId));
        UserProfileDTO dto = new UserProfileDTO();
        dto.setId(profile.getId());
        dto.setName(profile.getName());
        dto.setSurname(profile.getSurname());
        dto.setProfilePicture(profile.getProfilePicture());
        return dto;
    }

    /**
     * Retrieves the email and basic profile information of a user by their ID.
     *
     * @param userId the ID of the user
     * @return an EmailResponseDto containing the user's email, name, and surname
     * @throws NoSuchElementException if the user is not found
     */
    public EmailResponseDto getEmailById(Long userId) {
        UserProfile profile = repository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Not found " + userId));
        EmailResponseDto dto = new EmailResponseDto();
        dto.setEmail(profile.getEmail());
        dto.setName(profile.getName());
        dto.setSurname(profile.getSurname());
        return dto;
    }
}
