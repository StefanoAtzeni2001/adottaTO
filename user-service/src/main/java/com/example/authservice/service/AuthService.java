package com.example.authservice.service;

import com.example.authservice.dto.AuthRegisterResponseDTO;
import com.example.authservice.dto.AuthRegisterRequestDTO;
import com.example.authservice.model.Auth;
import com.example.authservice.model.UserProfile;
import com.example.authservice.repository.AuthRepository;
import com.example.authservice.repository.UserProfileRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Service
public class AuthService implements UserDetailsService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserProfileRepository userProfileRepository;
    private final JwtService jwtService;

    public AuthService(AuthRepository authRepository,
                       PasswordEncoder passwordEncoder,
                       UserProfileRepository userProfileRepository,
                       JwtService jwtService) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
        this.userProfileRepository = userProfileRepository;
        this.jwtService = jwtService;
    }

    /**
     * Loads user details by email for Spring Security authentication.
     * Throws exception if the user is not found or the account uses Google OAuth2 provider.
     * Generates a JWT token and logs it.
     *
     * @param email the email address of the user
     * @return UserDetails object required by Spring Security
     * @throws UsernameNotFoundException if a user is not found or uses Google login
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Auth auth = findAuthByEmailOrThrow(email);

        if ("google".equals(auth.getProvider())) {
            throw new UsernameNotFoundException("Please use Google login for this account.");
        }

        UserDetails userDetails = buildUserDetails(auth);

        String token = jwtService.generateToken(userDetails.getUsername());
        System.out.println("Generated token: " + token);

        return userDetails;
    }

    /**
     * Registers a new user with local authentication.
     * Validates that the email is not already registered.
     * Saves both the Auth entity and the UserProfile entity.
     *
     * @param request DTO containing registration information (email, password, name, surname)
     * @return DTO containing the registered user's id and email
     * @throws IllegalArgumentException if the email is already registered
     */
    @Transactional
    public AuthRegisterResponseDTO register(AuthRegisterRequestDTO request) {
        validateEmailNotRegistered(request.getEmail());

        Auth savedAuth = saveNewAuth(request);
        saveUserProfile(savedAuth, request);

        return buildRegisterResponse(savedAuth);
    }

    /**
     * Finds an existing Auth entity by email or creates a new one for Google OAuth2 users.
     *
     * @param email user's email
     * @return existing or newly created Auth entity
     */
    @Transactional
    public Auth findOrCreateAuthByEmail(String email) {
        return authRepository.findByEmail(email).orElseGet(() -> {
            Auth auth = new Auth();
            auth.setEmail(email);
            auth.setPassword("oauth2user"); // placeholder password, not used
            auth.setProvider("google");
            return authRepository.save(auth);
        });
    }

    /**
     * Finds an Auth entity by email.
     *
     * @param email the user's email
     * @return Optional containing the Auth if found, empty otherwise
     */
    public Optional<Auth> findByEmail(String email) {
        return authRepository.findByEmail(email);
    }

    /**
     * Registers a Google user if not already present in the database.
     * If the UserProfile does not exist for this user, it will be created.
     *
     * @param email   user's email
     * @param name    user's first name
     * @param surname user's last name
     */
    @Transactional
    public void registerGoogleUserIfNecessary(String email, String name, String surname, String picture) {
        Auth auth = findOrCreateAuthByEmail(email);
        userProfileRepository.findById(auth.getId())
                .orElseGet(() -> saveGoogleUserProfile(auth, email, name, surname, picture));
    }

    /**
     * Retrieves a UserProfile entity by the user's email.
     *
     * @param email the user's email address
     * @return the UserProfile associated with the user
     * @throws RuntimeException if a user or profile is not found
     */
    public UserProfile getUserProfileByEmail(String email) {
        Auth auth = findAuthByEmailOrThrow(email);
        return userProfileRepository.findById(auth.getId())
                .orElseThrow(() -> new RuntimeException("Profile not found for user id: " + auth.getId()));
    }

    // ------- Private helper methods --------

    /**
     * Finds an Auth entity by email or throws an exception if not found.
     *
     * @param email the user's email address
     * @return the Auth entity found
     * @throws UsernameNotFoundException if a user is not found
     */
    private Auth findAuthByEmailOrThrow(String email) {
        return authRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    /**
     * Validates that the email is not already registered.
     *
     * @param email the email to validate
     * @throws IllegalArgumentException if the email is already registered
     */
    private void validateEmailNotRegistered(String email) {
        if (authRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email is already registered.");
        }
    }

    /**
     * Builds a Spring Security UserDetails object from the Auth entity.
     *
     * @param auth the Auth entity
     * @return UserDetails object
     */
    private UserDetails buildUserDetails(Auth auth) {
        return new org.springframework.security.core.userdetails.User(
                auth.getEmail(),
                auth.getPassword(),
                Collections.emptyList()  // Replace it with roles/authorities if implemented
        );
    }

    /**
     * Saves a new Auth entity with an encoded password and local provider.
     *
     * @param request registration request DTO
     * @return the saved Auth entity
     */
    private Auth saveNewAuth(AuthRegisterRequestDTO request) {
        Auth user = new Auth();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setProvider("local");
        return authRepository.save(user);
    }

    /**
     * Saves a new UserProfile entity linked to the saved Auth entity.
     *
     * @param savedAuth the saved Auth entity
     * @param request   the registration request DTO
     */
    private void saveUserProfile(Auth savedAuth, AuthRegisterRequestDTO request) {
        UserProfile profile = new UserProfile();
        profile.setAuth(savedAuth);
        profile.setEmail(request.getEmail());
        profile.setName(request.getName());
        profile.setSurname(request.getSurname());
        userProfileRepository.save(profile);
    }

    /**
     * Builds the response DTO for registration containing id and email.
     *
     * @param savedAuth the saved Auth entity
     * @return AuthRegisterResponseDTO with id and email
     */
    private AuthRegisterResponseDTO buildRegisterResponse(Auth savedAuth) {
        return AuthRegisterResponseDTO.builder()
                .id(savedAuth.getId())
                .email(savedAuth.getEmail())
                .build();
    }

    /**
     * Saves a new UserProfile entity for a Google OAuth2 user.
     *
     * @param auth    the Auth entity
     * @param email   user's email
     * @param name    user's first name
     * @param surname user's last name
     * @return the saved UserProfile entity
     */
    private UserProfile saveGoogleUserProfile(Auth auth, String email, String name, String surname, String picture) {
        UserProfile profile = new UserProfile();
        profile.setAuth(auth);
        profile.setEmail(email);
        profile.setName(name);
        profile.setSurname(surname);
        profile.setProfilePicture(picture);
        return userProfileRepository.save(profile);
    }
}
