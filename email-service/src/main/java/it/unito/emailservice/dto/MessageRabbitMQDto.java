package it.unito.emailservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MessageRabbitMQDto {

    private final Long receiverId;
    private final Long senderId;
    private final String message;
    private final String type;
}
