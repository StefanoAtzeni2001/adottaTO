package it.unito.emailservice.service;

import org.example.shareddtos.dto.AdoptionPostRabbitMQDto;
import org.example.shareddtos.dto.EmailResponseDto;
import org.example.shareddtos.dto.EmailMessageRabbitMQDto;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Service responsible for handling incoming RabbitMQ messages related to:
 * - saved search matches,
 * - chat notifications,
 * - adoption requests and responses.
 */
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    private final UserServiceClient userServiceClient;

    /**
     * Constructs the EmailService with required dependencies.
     *
     * @param mailSender         used to send emails
     * @param userServiceClient  used to fetch user details via HTTP calls
     */
    public EmailService(JavaMailSender mailSender, UserServiceClient userServiceClient) {
        this.mailSender = mailSender;
        this.userServiceClient = userServiceClient;
    }

    /**
     * Listener for RabbitMQ messages indicating a new adoption post matching a user's saved search.
     *
     * @param adoptionPostRabbitMQDto the DTO containing adoption post details and userId
     */
    @RabbitListener(queues = "${app.rabbitmq.queue.savedsearch-match}")
    public void receiveMatch(AdoptionPostRabbitMQDto adoptionPostRabbitMQDto) {

        System.out.println("Nuovo post di interesse per " + adoptionPostRabbitMQDto.getUserId());

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
            body += "\nRazza: " + adoptionPostRabbitMQDto.getBreed();
        }
        if(adoptionPostRabbitMQDto.getAge() != null) {
            body += "\nEtà: " + adoptionPostRabbitMQDto.getAge();
        }
        if(adoptionPostRabbitMQDto.getGender() != null) {
            body += "\nGenere: " + adoptionPostRabbitMQDto.getGender();
        }
        if(adoptionPostRabbitMQDto.getColor() != null) {
            body += "\nColore: " + adoptionPostRabbitMQDto.getColor();
        }
        if(adoptionPostRabbitMQDto.getLocation() != null) {
            body += "\nPosizione: " + adoptionPostRabbitMQDto.getLocation();
        }

        System.out.println(subject);
        System.out.println(body);
        sendEmail(receiverUser.getEmail(), subject, body);
    }

    /**
     * Listener for chat-related email notifications (new messages, adoption requests, acceptances).
     *
     * @param message the email message DTO
     */
    @RabbitListener(queues = "${app.rabbitmq.queue.chat-notification}")
    public void receiveMessage(EmailMessageRabbitMQDto message) {

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

    /**
     * Sends an email notification for a new chat message.
     *
     * @param message DTO containing sender, receiver, and message content
     */
    private void handleNewMessage(EmailMessageRabbitMQDto message) {
        System.out.println("Nuovo messaggio ricevuto:"
                + "\nsenderId: " + message.getSenderId()
                + "\nreceiverId: " + message.getReceiverId()
                + "\nmessage: " + message.getMessage());

        EmailResponseDto receiverUser = userServiceClient.getUser(message.getReceiverId());
        EmailResponseDto senderUser = userServiceClient.getUser(message.getSenderId());

        System.out.println("Recuperate informazioni utenti:"
                + "\nEmail receiver: " + receiverUser.getEmail()
                + "\nNome receiver: " + receiverUser.getName()
                + "\nCognome receiver: " + receiverUser.getSurname()
                + "\nEmail sender: " + senderUser.getEmail()
                + "\nNome sender: " + senderUser.getName()
                + "\nCognome sender: " + senderUser.getSurname()
        );

        String subject = "Nuovo messaggio da " + senderUser.getName();
        String body = "Ciao " + receiverUser.getName() + "!\n"
                + "Hai ricevuto un nuovo messaggio da " + senderUser.getName() + " " +  senderUser.getSurname() + ":\n"
                + message.getMessage();

        System.out.println(subject);
        System.out.println(body);
        sendEmail(receiverUser.getEmail(), subject, body);
        //sendEmail("evina2.ef@gmail.com", "Test email", "This is a test email!");

    }

    /**
     * Sends an email notification for an adoption request event (send or cancel).
     *
     * @param message DTO containing adoption request details and interested users
     */
    private void handleAdoptionRequest(EmailMessageRabbitMQDto message) {
        System.out.println("Richiesta adozione ricevuta:"
                + "\nsenderId: " + message.getSenderId()
                + "\nreceiverId: " + message.getReceiverId()
                + "\nmessage: " + message.getMessage());

        EmailResponseDto receiverUser = userServiceClient.getUser(message.getReceiverId());
        EmailResponseDto senderUser = userServiceClient.getUser(message.getSenderId());

        System.out.println("Recuperate informazioni utenti:"
                + "\nEmail receiver: " + receiverUser.getEmail()
                + "\nNome receiver: " + receiverUser.getName()
                + "\nCognome receiver: " + receiverUser.getSurname()
                + "\nEmail sender: " + senderUser.getEmail()
                + "\nNome sender: " + senderUser.getName()
                + "\nCognome sender: " + senderUser.getSurname()
        );

        String subject = null;
        String body = null;
        switch (message.getMessage()) {
            case "send" -> {
                subject = "Nuova richiesta di adozione da " + senderUser.getName();
                body = "Ciao " + receiverUser.getName() + "!\n"
                        + "Hai ricevuto una richiesta di adozione da " + senderUser.getName() + " " + senderUser.getSurname() + ":\n";
            }
            case "cancel" -> {
                subject = "Richiesta di adozione annullata da " + senderUser.getName();
                body = "Ciao " + receiverUser.getName() + "!\n"
                        + senderUser.getName() + " " + senderUser.getSurname() + " ha annullato la sua richiesta di adozione.\n";
            }
        }

        System.out.println(subject);
        System.out.println(body);
        sendEmail(receiverUser.getEmail(), subject, body);
    }

    /**
     * Sends an email notification for adoption request acceptance or rejection.
     *
     * @param message DTO containing acceptance/rejection details and interested user
     */
    private void handleAdoptionAccept(EmailMessageRabbitMQDto message) {
        System.out.println("Accettazione adozione ricevuta:"
                + "\nsenderId: " + message.getSenderId()
                + "\nreceiverId: " + message.getReceiverId()
                + "\nmessage: " + message.getMessage());

        EmailResponseDto receiverUser = userServiceClient.getUser(message.getReceiverId());
        EmailResponseDto senderUser = userServiceClient.getUser(message.getSenderId());

        System.out.println("Recuperate informazioni utenti:"
                + "\nEmail receiver: " + receiverUser.getEmail()
                + "\nNome receiver: " + receiverUser.getName()
                + "\nCognome receiver: " + receiverUser.getSurname()
                + "\nEmail sender: " + senderUser.getEmail()
                + "\nNome sender: " + senderUser.getName()
                + "\nCognome sender: " + senderUser.getSurname()
        );

        String subject = null;
        String body = null;
        switch (message.getMessage()) {
            case "accept" -> {
                subject = "Richiesta di adozione accettata da " + senderUser.getName();
                body = "Ciao " + receiverUser.getName() + "!\n"
                        + senderUser.getName() + " " + senderUser.getSurname() + " ha accettato la tua richiesta di adozione.\n";
            }
            case "reject" -> {
                subject = "Richiesta di adozione rifiutata da " + senderUser.getName();
                body = "Ciao " + receiverUser.getName() + "!\n"
                        + senderUser.getName() + " " + senderUser.getSurname() + " ha rifiutato la tua richiesta di adozione.\n";
            }
        }

        System.out.println(subject);
        System.out.println(body);
        sendEmail(receiverUser.getEmail(), subject, body);
    }

    /**
     * Sends a simple email using the configured JavaMailSender.
     *
     * @param toEmail recipient email address
     * @param subject email subject
     * @param body    email body content
     */
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
