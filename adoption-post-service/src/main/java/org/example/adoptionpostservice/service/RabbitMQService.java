package org.example.adoptionpostservice.service;

import org.example.shareddtos.dto.RequestAcceptedMessageRabbitMQDto;
import org.example.adoptionpostservice.model.AdoptionPost;
import org.example.adoptionpostservice.repository.AdoptionPostRepository;
import org.example.shareddtos.dto.AdoptionPostSummaryDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Service responsible for interacting with RabbitMQ.
 * It sends events when a new adoption post is created and handles messages when an adoption request is accepted.
 */
@Component
public class RabbitMQService {

    @Value("${app.rabbitmq.exchange}")
    private String adottatoExchange;

    @Value("${app.rabbitmq.routingkey.new-post}")
    private String newPostRoutingKey;

    private final AdoptionPostRepository repository;

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQService(AdoptionPostRepository repository, RabbitTemplate rabbitTemplate) {
        this.repository = repository;
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Sends a new adoption post event to the specified RabbitMQ exchange using the routing key
     *
     * @param dto the adoption post summary data to send
     */
    public void sendNewPostEvent(AdoptionPostSummaryDto dto) {
        rabbitTemplate.convertAndSend(adottatoExchange, newPostRoutingKey, dto);
    }

    /**
     * Handles incoming RabbitMQ messages when an adoption request is accepted.
     * It sets the adopter ID on the corresponding adoption post
     *
     * @param message the message containing the adoption post ID and adopter ID
     */
    @RabbitListener(queues = "${app.rabbitmq.queue.chat-request-accepted}")
    public void handleAcceptedRequest(RequestAcceptedMessageRabbitMQDto message) {
        System.out.println("Ricevuto messaggio chat.request.accepted:" + message.getAdoptionPostId() + " " + message.getAdopterId());

        AdoptionPost post = repository.findById(message.getAdoptionPostId()).orElse(null);

        System.out.println("Ricevuta richiesta accettata!!");
        if(post != null) {
            post.setAdopterId(message.getAdopterId());
            post.setActive(false);
            repository.save(post);
        }
    }
}
