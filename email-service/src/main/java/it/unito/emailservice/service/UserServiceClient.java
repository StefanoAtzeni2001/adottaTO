package it.unito.emailservice.service;

import it.unito.emailservice.dto.EmailRequestDto;
import it.unito.emailservice.dto.EmailResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class UserServiceClient {

    private final WebClient webClient;

    public UserServiceClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://userservice:8080")
                .build();
    }

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
