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

    private final Map<String, String> routingKeys;

    private final RabbitTemplate rabbitTemplate;

    public SenderRabbitMQService(
            RabbitTemplate rabbitTemplate,
            @Value("${app.rabbitmq.routingkey.new-message}") String newMessageKey,
            @Value("${app.rabbitmq.routingkey.request}") String requestKey,
            @Value("${app.rabbitmq.routingkey.accept}") String acceptKey
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.routingKeys = Map.of(
                "new-message", newMessageKey,
                "request", requestKey,
                "accept", acceptKey
        );
    }

    public void sendNewMessageEmail(Long receiverId, Long senderId, String message){
        String routingKey = routingKeys.get("new-message");

        MessageRabbitMQ messageRabbitMQ = new MessageRabbitMQ(receiverId, senderId, message);
        rabbitTemplate.convertAndSend(exchange, routingKey, messageRabbitMQ);
        System.out.println("Sent new-message to " + messageRabbitMQ.getReceiverId());

    }

    public void sendRequestEmail(Long ownerId, Long adopterId, Long ownerID, String message){
        String routingKey = routingKeys.get("request");

        MessageRabbitMQ messageRabbitMQ = new MessageRabbitMQ(ownerId, adopterId, message);
        rabbitTemplate.convertAndSend(exchange, routingKey, messageRabbitMQ);
        System.out.println("Sent request " + message + " to " + messageRabbitMQ.getReceiverId());

    }

    public void sendAcceptEmail(Long ownerId, Long adopterId, String message){
        String routingKey = routingKeys.get("accept");

        MessageRabbitMQ messageRabbitMQ = new MessageRabbitMQ(ownerId, adopterId, message);
        rabbitTemplate.convertAndSend(exchange, routingKey, messageRabbitMQ);
        System.out.println("Sent accept to " + messageRabbitMQ.getReceiverId());

    }
}
