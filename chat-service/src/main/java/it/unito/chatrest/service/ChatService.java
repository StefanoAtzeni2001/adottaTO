package it.unito.chatrest.service;

import it.unito.chatrest.dto.MessageSendRequest;
import it.unito.chatrest.model.Chat;
import it.unito.chatrest.model.Message;
import it.unito.chatrest.repository.ChatRepository;
import it.unito.chatrest.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private SenderRabbitMQService senderRabbitMQService;

    public Message sendMessage(MessageSendRequest request) {
        Chat chat;

        if (request.getChatId() != null) {
            // Caso 1: chatId presente → recupera la chat
            chat = chatRepository.findById(request.getChatId())
                    .orElseThrow(() -> new IllegalArgumentException("Chat " + request.getChatId() + " not found"));
        } else {
            // Caso 2: chatId assente → verifica se esiste già chat tra sender e receiver per il post
            if (request.getAdoptionPostId() == null) {
                throw new IllegalArgumentException("Adoption post not found");
            }

            chat = chatRepository.findByAdopterIdAndOwnerIdAndAdoptionPostId(
                    request.getSenderId(),
                    request.getReceiverId(),
                    request.getAdoptionPostId()
            ).orElseGet(() -> {
                Chat newChat = new Chat();
                newChat.setAdopterId(request.getSenderId());
                newChat.setOwnerId(request.getReceiverId());
                newChat.setAdoptionPostId(request.getAdoptionPostId());
                newChat.setRequestFlag(false);
                newChat.setAcceptedFlag(false);
                return chatRepository.save(newChat);
            });
        }

        // Crea e salva il messaggio
        Message message = new Message();
        message.setChat(chat);
        message.setSenderId(request.getSenderId());
        message.setReceiverId(request.getReceiverId());
        message.setMessage(request.getMessage());
        message.setTimeStamp(LocalDateTime.now());
        message.setSeen(false);

        senderRabbitMQService.sendNewMessageEmail(request.getSenderId(), request.getReceiverId(), request.getMessage());

        return messageRepository.save(message);
    }


    public List<Chat> getChatsForUser(Long userId) {
        List<Chat> chats = chatRepository.findByOwnerIdOrAdopterId(userId, userId);

        // Mappa le chat al loro ultimo messaggio
        chats.sort((c1, c2) -> {
            Message m1 = messageRepository.findFirstByChatIdOrderByTimeStampDesc(c1.getId());
            Message m2 = messageRepository.findFirstByChatIdOrderByTimeStampDesc(c2.getId());

            // Chat senza messaggi vanno in fondo
            if (m1 == null && m2 == null) return 0;
            if (m1 == null) return 1;
            if (m2 == null) return -1;

            return m2.getTimeStamp().compareTo(m1.getTimeStamp());
        });

        return chats;
    }


    // Tutta la chat e marca quelli ricevuti da userId come letti
    public List<Message> getChatMessagesAndMarkSeen(Long chatId, Long userId) {
        // 1. Trova i messaggi ricevuti non letti
        List<Message> unreadMessages = messageRepository.findByChatIdAndReceiverIdAndSeenFalse(chatId, userId);

        // 2. Marcarli come letti
        for (Message m : unreadMessages) {
            m.setSeen(true);
        }

        // 3. Salva aggiornamenti in batch
        messageRepository.saveAll(unreadMessages);

        // 4. Restituisci tutti i messaggi ordinati
        return messageRepository.findByChatIdOrderByTimeStampAsc(chatId);
    }


    // Solo nuovi messaggi non letti ricevuti da userId e marca come letti
    public List<Message> getUnreadMessagesAndMarkSeen(Long chatId, Long userId) {
        List<Message> unreadMessages = messageRepository.findByChatIdAndReceiverIdAndSeenFalse(
                chatId, userId
        );

        for (Message msg : unreadMessages) {
            msg.setSeen(true);
        }

        messageRepository.saveAll(unreadMessages);
        return unreadMessages;
    }

    public String sendRequest(Long chatId, Long adopterId) {
        Chat chat = chatRepository.findById(chatId).orElse(null);

        if (chat == null) {
            return "Chat non trovata";
        }

        if (!chat.getAdopterId().equals(adopterId)) {
            return "Solo l'adopter può inviare la proposta";
        }

        chat.setRequestFlag(true);
        chatRepository.save(chat);

        senderRabbitMQService.sendRequestEmail(chatId, adopterId, chat.getOwnerId(), "send");

        return "Proposta inviata con successo";
    }

    public String cancelRequest(Long chatId, Long userId) {
        Chat chat = chatRepository.findById(chatId).orElse(null);

        if (chat == null) {
            return "Chat non trovata";
        }

        if (!chat.getAdopterId().equals(userId)) {
            return "Solo l'adopter può ritirare la proposta";
        }

        if (!chat.isRequestFlag()) {
            return "Proposta non ancora inviata";
        }

        chat.setRequestFlag(false);
        chatRepository.save(chat);

        return "Proposta cancellata con successo";
    }

    public String acceptRequest(Long chatId, Long userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(null);

        if (chat == null) {
            return "Chat non trovata";
        }

        if (!chat.getOwnerId().equals(userId)) {
            return "Solo l'owner può accettare la proposta";
        }

        if (!chat.isRequestFlag()) {
            return "Proposta non ancora inviata";
        }

        chat.setAcceptedFlag(true);
        chatRepository.save(chat);

        return "Richiesta accettata con successo";
    }

    public String rejectRequest(Long chatId, Long userId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(null);

        if (chat == null) {
            return "Chat non trovata";
        }

        if (!chat.getOwnerId().equals(userId)) {
            return "Solo l'owner può rifiutare la proposta";
        }

        if (!chat.isRequestFlag()) {
            return "Proposta non ancora inviata";
        }

        chat.setRequestFlag(false);
        chatRepository.save(chat);

        return "Richiesta rifiutata con successo";
    }

}
