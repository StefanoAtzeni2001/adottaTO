package org.example.adoptionpostservice.service;

import org.example.adoptionpostservice.dto.RequestAcceptedMessageRabbitMQDto;
import org.example.adoptionpostservice.model.AdoptionPost;
import org.example.adoptionpostservice.repository.AdoptionPostRepository;
import org.example.shareddtos.dto.AdoptionPostSummaryDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    public void sendNewPostEvent(AdoptionPostSummaryDto dto) {
        rabbitTemplate.convertAndSend(adottatoExchange, newPostRoutingKey, dto);
    }

    @RabbitListener(queues = "${app.rabbitmq.queue.chat-request-accepted}")
    public void handleAcceptedRequest(RequestAcceptedMessageRabbitMQDto message) {
        System.out.println("Ricevuto messaggio chat.request.accepted:" + message.getAdoptionPostId() + " " + message.getAdopterId());

        AdoptionPost post = repository.findById(message.getAdoptionPostId()).orElse(null);

        System.out.println("Ricevuta richiesta accettata!!");
        if(post != null) {
            post.setAdopterId(message.getAdopterId());

            repository.save(post);
        }

    }

}
