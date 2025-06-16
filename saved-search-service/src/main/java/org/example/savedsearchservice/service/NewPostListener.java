package org.example.savedsearchservice.service;


import org.example.savedsearchservice.repository.SavedSearchRepository;
import org.example.shareddtos.dto.AdoptionPostSummaryDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NewPostListener {



    private final SavedSearchRepository repository;

    public NewPostListener(SavedSearchRepository repository) {
        this.repository = repository;
    }
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
    }
}
