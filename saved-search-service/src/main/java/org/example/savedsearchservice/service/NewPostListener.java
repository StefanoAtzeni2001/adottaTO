package org.example.savedsearchservice.service;


import org.example.shareddtos.dto.AdoptionPostSummaryDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NewPostListener {
    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void handleNewAdoptionPost(AdoptionPostSummaryDto post) {
        System.out.println("Ricevuto nuovo post da RabbitMQ:");
        System.out.println(post);
    }
}
