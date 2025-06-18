package it.unito.emailservice.service;

import it.unito.emailservice.dto.MessageRabbitMQ;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EmailService {

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void receiveMessage(MessageRabbitMQ message) {
        System.out.println("Received: " + message);
    }
}