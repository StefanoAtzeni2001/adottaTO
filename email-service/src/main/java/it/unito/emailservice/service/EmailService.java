package it.unito.emailservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unito.emailservice.dto.MessageRabbitMQ;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Component
public class EmailService {

    private final ObjectMapper objectMapper;



    public EmailService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @RabbitListener(queues = "${app.rabbitmq.queue}")
    public void receiveMessage(MessageRabbitMQ message) {
        try {

            String type = message.getType();

            switch (type) {
                case "new-message":
                    handleNewMessage(message);
                    break;
                case "adoption-request":
                    handleAdoptionRequest(message);
                    break;
                case "adoption-accept":
                    handleAdoptionAccept(message);
                    break;
                default:
                    System.out.println("Unhandle request: " + type);
            }
        } catch (Exception e) {
            System.err.println("Handle message error: " + e.getMessage());
        }
    }

    private void handleNewMessage(MessageRabbitMQ message) {
        System.out.println("Nuovo messaggio ricevuto: " + message);
        // invio email o altra logica
    }

    private void handleAdoptionRequest(MessageRabbitMQ message) {
        System.out.println("Richiesta adozione ricevuta: " + message);
        // logica per richiesta
    }

    private void handleAdoptionAccept(MessageRabbitMQ message) {
        System.out.println("Accettazione adozione ricevuta: " + message);
        // logica per accettazione
    }
}
