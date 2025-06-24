package org.example.shareddtos.dto;

import lombok.*;

/**
 * DTO used to notification adoptions
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RequestAcceptedMessageRabbitMQDto {

    private Long adoptionPostId;
    private Long adopterId;

}
