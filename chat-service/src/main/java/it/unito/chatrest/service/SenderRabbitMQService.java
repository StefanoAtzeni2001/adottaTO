package it.unito.chatrest.service;

import it.unito.chatrest.dto.MessageRabbitMQ;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SenderRabbitMQService {

    @Value("${app.rabbitmq.exchange}")
    private String exchange;

    @Value("${app.rabbitmq.routingkey.chat-notification}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    public SenderRabbitMQService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendNewMessageEmail(Long receiverId, Long senderId, String message){

        MessageRabbitMQ messageRabbitMQ = new MessageRabbitMQ(receiverId, senderId, message, "new-message");
        rabbitTemplate.convertAndSend(exchange, routingKey, messageRabbitMQ);
        System.out.println("Sent new-message to " + messageRabbitMQ.getReceiverId());

    }

    public void sendRequestEmail(Long ownerId, Long adopterId, String message){

        MessageRabbitMQ messageRabbitMQ = new MessageRabbitMQ(ownerId, adopterId, message, "adoption-request");
        rabbitTemplate.convertAndSend(exchange, routingKey, messageRabbitMQ);
        System.out.println("Sent request " + message + " to " + messageRabbitMQ.getReceiverId());

    }

    public void sendAcceptEmail(Long ownerId, Long adopterId, String message){

        MessageRabbitMQ messageRabbitMQ = new MessageRabbitMQ(ownerId, adopterId, message, "adoption-accept");
        rabbitTemplate.convertAndSend(exchange, routingKey, messageRabbitMQ);
        System.out.println("Sent accept to " + messageRabbitMQ.getReceiverId());

    }
}

