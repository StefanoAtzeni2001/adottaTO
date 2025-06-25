package it.unito.chatrest.config;

import it.unito.chatrest.dto.MessageSendRequest;
import it.unito.chatrest.repository.ChatRepository;
import it.unito.chatrest.service.ChatService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final ChatRepository chatRepository;
    private final ChatService chatService;

    public DataInitializer(ChatRepository chatRepository, ChatService chatService) {
        this.chatRepository = chatRepository;
        this.chatService = chatService;
    }

    @Override
    public void run(String... args) throws Exception {
        if (chatRepository.count() == 0) {

            // Chat tra utente 1 e 2
            MessageSendRequest msg1 = new MessageSendRequest();
            msg1.setSenderId(1L);
            msg1.setReceiverId(2L);
            msg1.setAdoptionPostId(3L);
            msg1.setMessage("Ciao, sono interessato all'annuncio!");
            chatService.sendMessage(msg1, 1L);

            MessageSendRequest msg2 = new MessageSendRequest();
            msg2.setSenderId(2L);
            msg2.setReceiverId(1L);
            msg2.setChatId(1L);
            msg2.setMessage("Ciao! L'annuncio è ancora disponibile.");
            chatService.sendMessage(msg2, 2L);

            // Chat tra utente 1 e 3
            MessageSendRequest msg3 = new MessageSendRequest();
            msg3.setSenderId(1L);
            msg3.setReceiverId(3L);
            msg3.setAdoptionPostId(9L);
            msg3.setMessage("Buongiorno! Mi piacerebbe adottare il tuo animale.");
            chatService.sendMessage(msg3, 1L);

            MessageSendRequest msg4 = new MessageSendRequest();
            msg4.setSenderId(3L);
            msg4.setReceiverId(1L);
            msg4.setChatId(2L);
            msg4.setMessage("Salve! Possiamo parlarne meglio qui.");
            chatService.sendMessage(msg4, 3L);

            System.out.println(">>> Chat DB initialized");
        }
    }
}
