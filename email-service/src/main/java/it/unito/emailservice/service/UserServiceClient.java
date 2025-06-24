package it.unito.emailservice.service;

import org.example.shareddtos.dto.EmailRequestDto;
import org.example.shareddtos.dto.EmailResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;

/**
 * Component responsible for communicating with user-service
 * to retrieve user information (email, name, surname) using a WebClient
 */
@Component
public class UserServiceClient {

    private final WebClient webClient;

    public UserServiceClient(WebClient.Builder builder,
                             @Value("${app.webclient.userservice}") String userServiceBaseUrl) {
        this.webClient = builder
                .baseUrl(userServiceBaseUrl) //"http://user-service:8083"
                .build();
    }

    /**
     * Sends a POST request to the user-service
     *
     * @param userId the ID of the user whose info is needed
     * @return an EmailResponseDto containing name, surname, and email of the user
     */
    public EmailResponseDto getUser(Long userId) {

        EmailRequestDto request = new EmailRequestDto(userId);

        return webClient.post()
                .uri("/profile-email")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(EmailResponseDto.class)
                .block();
    }
}
