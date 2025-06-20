package it.unito.emailservice.service;

import it.unito.emailservice.dto.EmailRequestDTO;
import it.unito.emailservice.dto.EmailResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UserServiceClient {

    private final WebClient webClient;

    public UserServiceClient(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://userservice:8080")
                .build();
    }

    public EmailResponseDTO getUser(Long userId) {

        EmailRequestDTO request = new EmailRequestDTO(userId);

        return webClient.post()
                .uri("/profile-email")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(EmailResponseDTO.class)
                .block();
    }
}
