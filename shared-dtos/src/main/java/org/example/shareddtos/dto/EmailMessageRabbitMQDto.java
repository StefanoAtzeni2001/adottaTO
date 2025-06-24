package org.example.shareddtos.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO used to transfer chat notifications via RabbitMQ
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmailMessageRabbitMQDto {

    private Long receiverId;
    private Long senderId;
    private String message;
    private String type;

}
