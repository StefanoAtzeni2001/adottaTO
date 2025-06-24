package it.unito.chatrest.service;

import org.example.shareddtos.dto.EmailMessageRabbitMQDto;
import org.example.shareddtos.dto.RequestAcceptedMessageRabbitMQDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Service responsible for sending messages to RabbitMQ queues.
 * It encapsulates messaging logic for chat notifications, adoption requests, and request acceptance.
 */
@Component
public class SenderRabbitMQService {

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routingkey.chat-notification}")
    private String chatNotificationRoutingKey;

    @Value("${app.rabbitmq.routingkey.chat-request-accepted}")
    private String chatRequestAcceptedRoutingKey;

    private final RabbitTemplate rabbitTemplate;

    public SenderRabbitMQService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Sends a notification about a new chat message to the receiver via RabbitMQ.
     *
     * @param receiverId the ID of the message receiver
     * @param senderId   the ID of the message sender
     * @param message    the content of the message
     */
    public void sendNewMessageEmail(Long receiverId, Long senderId, String message){

        EmailMessageRabbitMQDto emailMessageRabbitMQDto = new EmailMessageRabbitMQDto(receiverId, senderId, message, "new-message");
        rabbitTemplate.convertAndSend(exchange, chatNotificationRoutingKey, emailMessageRabbitMQDto);
        System.out.println("Sent new-message to " + emailMessageRabbitMQDto.getReceiverId());

    }

    /**
     * Sends a notification about an adoption request (send or cancel) to the owner via RabbitMQ.
     *
     * @param ownerId    the ID of the pet owner receiving the request
     * @param adopterId  the ID of the adopter sending/canceling the request
     * @param message    the type of request action
     */
    public void sendRequestEmail(Long ownerId, Long adopterId, String message){

        EmailMessageRabbitMQDto emailMessageRabbitMQDto = new EmailMessageRabbitMQDto(ownerId, adopterId, message, "adoption-request");
        rabbitTemplate.convertAndSend(exchange, chatNotificationRoutingKey, emailMessageRabbitMQDto);
        System.out.println("Sent request " + message + " to " + emailMessageRabbitMQDto.getReceiverId());

    }

    /**
     * Sends a notification to the adopter when an adoption request is accepted or rejected.
     *
     * @param ownerId    the ID of the pet owner
     * @param adopterId  the ID of the adopter
     * @param message    the action taken ("accept" or "reject")
     */
    public void sendAcceptEmail(Long ownerId, Long adopterId, String message){

        EmailMessageRabbitMQDto emailMessageRabbitMQDto = new EmailMessageRabbitMQDto(ownerId, adopterId, message, "adoption-accept");
        rabbitTemplate.convertAndSend(exchange, chatNotificationRoutingKey, emailMessageRabbitMQDto);
        System.out.println("Sent accept to " + emailMessageRabbitMQDto.getReceiverId());

    }

    /**
     * Sends a system-wide event indicating that an adoption request has been accepted.
     *
     * @param adoptionPostId the ID of the adoption post
     * @param adopterId      the ID of the adopter
     */
    public void sendRequestAccepted(Long adoptionPostId, Long adopterId){

        RequestAcceptedMessageRabbitMQDto requestAcceptedMessageRabbitMQDto = new RequestAcceptedMessageRabbitMQDto(adoptionPostId, adopterId);
        rabbitTemplate.convertAndSend(exchange, chatRequestAcceptedRoutingKey, requestAcceptedMessageRabbitMQDto);
        System.out.println("Sent adoption: " + requestAcceptedMessageRabbitMQDto);

    }
}

