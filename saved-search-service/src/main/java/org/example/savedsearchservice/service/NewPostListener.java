package org.example.savedsearchservice.service;


import org.example.savedsearchservice.repository.SavedSearchRepository;
import org.example.shareddtos.dto.AdoptionPostRabbitMQDto;
import org.example.shareddtos.dto.AdoptionPostSummaryDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Component responsible for handling new adoption post events received from RabbitMQ.
 */
@Component
public class NewPostListener {

    @Value("${app.rabbitmq.exchange}")
    private String adottatoExchange;

    @Value("${app.rabbitmq.routingkey.savedsearch-match}")
    private String savedSearchMatchRoutingKey;

    private final RabbitTemplate rabbitTemplate;

    private final SavedSearchRepository repository;

    public NewPostListener(RabbitTemplate rabbitTemplate, SavedSearchRepository repository) {
        this.rabbitTemplate = rabbitTemplate;
        this.repository = repository;
    }

    /**
     * Listener method that is triggered when a new adoption post is received from RabbitMQ
     * It queries the database for users whose saved searches match the given post attributes and
     * sends a notification event for each matching user.
     *
     * @param post the adoption post received from the queue
     */
    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void handleNewAdoptionPost(AdoptionPostSummaryDto post) {
        List<Long> userIds = repository.findMatchingUserIds(
                post.getSpecies(),
                post.getBreed(),
                post.getGender(),
                post.getAge(),
                post.getColor()
        );

        System.out.println("Ricevuto nuovo post da RabbitMQ:");
        System.out.println(post);
        System.out.println("Matching ids: " + userIds);

        AdoptionPostRabbitMQDto postUserId = new AdoptionPostRabbitMQDto();
        postUserId.setAge(post.getAge());
        postUserId.setGender(post.getGender());
        postUserId.setBreed(post.getBreed());
        postUserId.setSpecies(post.getSpecies());
        postUserId.setName(post.getName());
        postUserId.setLocation(post.getLocation());
        postUserId.setColor(post.getColor());

        for(Long userId : userIds) {
            postUserId.setUserId(userId);
            rabbitTemplate.convertAndSend(adottatoExchange, savedSearchMatchRoutingKey, postUserId);
        }

        System.out.println("Inviato messaggio per notifica email");
    }
}
