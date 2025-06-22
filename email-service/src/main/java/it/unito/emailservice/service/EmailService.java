package it.unito.emailservice.service;

import it.unito.emailservice.dto.AdoptionPostRabbitMQDto;
import it.unito.emailservice.dto.EmailResponseDto;
import it.unito.emailservice.dto.MessageRabbitMQDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private UserServiceClient userServiceClient;

    @RabbitListener(queues = "${app.rabbitmq.queue.savedsearch-match}")
    public void receiveMatch(AdoptionPostRabbitMQDto adoptionPostRabbitMQDto) {

        System.out.println("Nuovo post ricevuto: " + adoptionPostRabbitMQDto);

        EmailResponseDto receiverUser = userServiceClient.getUser(adoptionPostRabbitMQDto.getUserId());

        String subject = "Nuovo annuncio corrispondente alla tua ricerca";
        String body = "Ciao " + receiverUser.getName() + "!\n"
                + "È stato pubblicato un nuovo annuncio corrispondente ai tuoi criteri di ricerca. Ecco le caratteristiche:";

        if(adoptionPostRabbitMQDto.getName() != null) {
            body += "\nNome: " + adoptionPostRabbitMQDto.getName();
        }
        if(adoptionPostRabbitMQDto.getSpecies() != null) {
            body += "\nSpecie: " + adoptionPostRabbitMQDto.getSpecies();
        }
        if(adoptionPostRabbitMQDto.getBreed() != null) {
            body += "\nBreed: " + adoptionPostRabbitMQDto.getBreed();
        }
        if(adoptionPostRabbitMQDto.getAge() != null) {
            body += "\nAge: " + adoptionPostRabbitMQDto.getAge();
        }
        if(adoptionPostRabbitMQDto.getGender() != null) {
            body += "\nGender: " + adoptionPostRabbitMQDto.getGender();
        }
        if(adoptionPostRabbitMQDto.getColor() != null) {
            body += "\nColor: " + adoptionPostRabbitMQDto.getColor();
        }
        if(adoptionPostRabbitMQDto.getLocation() != null) {
            body += "\nLocation: " + adoptionPostRabbitMQDto.getLocation();
        }

        System.out.println(subject);
        System.out.println(body);
        sendEmail(receiverUser.getEmail(), subject, body);
    }

    @RabbitListener(queues = "${app.rabbitmq.queue.chat-notification}")
    public void receiveMessage(MessageRabbitMQDto message) {

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

    private void handleNewMessage(MessageRabbitMQDto message) {
        System.out.println("Nuovo messaggio ricevuto: " + message);

        EmailResponseDto receiverUser = userServiceClient.getUser(message.getReceiverId());
        EmailResponseDto senderUser = userServiceClient.getUser(message.getSenderId());

        String subject = "Nuovo messaggio da " + senderUser.getName();
        String body = "Ciao " + receiverUser.getName() + "!\n"
                + "Hai ricevuto un nuovo messaggio da " + senderUser.getName() + " " +  senderUser.getSurname() + ":\n"
                + message.getMessage();

        System.out.println(subject);
        System.out.println(body);
        sendEmail(receiverUser.getEmail(), subject, body);
        //sendEmail("evina2.ef@gmail.com", "Test email", "This is a test email!");

    }

    private void handleAdoptionRequest(MessageRabbitMQDto message) {
        System.out.println("Richiesta adozione ricevuta: " + message);

        EmailResponseDto receiverUser = userServiceClient.getUser(message.getReceiverId());
        EmailResponseDto senderUser = userServiceClient.getUser(message.getSenderId());

        String subject = null;
        String body = null;
        switch (message.getMessage()){
            case "send":
                subject = "Nuova richiesta di adozione da " + senderUser.getName();
                body = "Ciao " + receiverUser.getName() + "!\n"
                        + "Hai ricevuto una richiesta di adozione da " + senderUser.getName() + " " +  senderUser.getSurname() + ":\n";
                break;
            case "cancel":
                subject = "Richiesta di adozione annullata da " + senderUser.getName();
                body = "Ciao " + receiverUser.getName() + "!\n"
                        + senderUser.getName() + " " +  senderUser.getSurname() + " ha annullato la sua richiesta di adozione.\n";
                break;
        }

        System.out.println(subject);
        System.out.println(body);
        sendEmail(receiverUser.getEmail(), subject, body);
    }

    private void handleAdoptionAccept(MessageRabbitMQDto message) {
        System.out.println("Accettazione adozione ricevuta: " + message);

        EmailResponseDto receiverUser = userServiceClient.getUser(message.getReceiverId());
        EmailResponseDto senderUser = userServiceClient.getUser(message.getSenderId());

        String subject = null;
        String body = null;
        switch (message.getMessage()){
            case "accept":
                subject = "Richiesta di adozione accettata da " + senderUser.getName();
                body = "Ciao " + receiverUser.getName() + "!\n"
                        + senderUser.getName() + " " +  senderUser.getSurname() + " ha accettato la tua richiesta di adozione.\n";
                break;
            case "reject":
                subject = "Richiesta di adozione rifiutata da " + senderUser.getName();
                body = "Ciao " + receiverUser.getName() + "!\n"
                        + senderUser.getName() + " " +  senderUser.getSurname() + " ha rifiutato la tua richiesta di adozione.\n";
                break;
        }

        System.out.println(subject);
        System.out.println(body);
        sendEmail(receiverUser.getEmail(), subject, body);
    }


    public void sendEmail(String toEmail, String subject, String body){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("adottato.taass@gmail.com");
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
        System.out.println("Email sent successfully!");
    }
}
