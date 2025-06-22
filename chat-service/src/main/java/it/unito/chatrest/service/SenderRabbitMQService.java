package it.unito.chatrest.service;

import it.unito.chatrest.dto.EmailMessageRabbitMQ;
import it.unito.chatrest.dto.RequestAcceptedMessageRabbitMQDto;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    public void sendNewMessageEmail(Long receiverId, Long senderId, String message){

        EmailMessageRabbitMQ emailMessageRabbitMQ = new EmailMessageRabbitMQ(receiverId, senderId, message, "new-message");
        rabbitTemplate.convertAndSend(exchange, chatNotificationRoutingKey, emailMessageRabbitMQ);
        System.out.println("Sent new-message to " + emailMessageRabbitMQ.getReceiverId());

    }

    public void sendRequestEmail(Long ownerId, Long adopterId, String message){

        EmailMessageRabbitMQ emailMessageRabbitMQ = new EmailMessageRabbitMQ(ownerId, adopterId, message, "adoption-request");
        rabbitTemplate.convertAndSend(exchange, chatNotificationRoutingKey, emailMessageRabbitMQ);
        System.out.println("Sent request " + message + " to " + emailMessageRabbitMQ.getReceiverId());

    }

    public void sendAcceptEmail(Long ownerId, Long adopterId, String message){

        EmailMessageRabbitMQ emailMessageRabbitMQ = new EmailMessageRabbitMQ(ownerId, adopterId, message, "adoption-accept");
        rabbitTemplate.convertAndSend(exchange, chatNotificationRoutingKey, emailMessageRabbitMQ);
        System.out.println("Sent accept to " + emailMessageRabbitMQ.getReceiverId());

    }

    public void sendRequestAccepted(Long adoptionPostId, Long adopterId){

        RequestAcceptedMessageRabbitMQDto requestAcceptedMessageRabbitMQDto = new RequestAcceptedMessageRabbitMQDto(adoptionPostId, adopterId);
        rabbitTemplate.convertAndSend(exchange, chatRequestAcceptedRoutingKey, requestAcceptedMessageRabbitMQDto);
        System.out.println("Sent adoption: " + requestAcceptedMessageRabbitMQDto);

    }
}

