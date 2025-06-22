package it.unito.chatrest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class RequestAcceptedMessageRabbitMQDto {
    private final Long adoptionPostId;
    private final Long adopterId;
}
