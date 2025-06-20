package it.unito.chatrest.service;

import it.unito.chatrest.dto.MessageSendRequest;
import it.unito.chatrest.model.Chat;
import it.unito.chatrest.model.Message;
import it.unito.chatrest.repository.ChatRepository;
import it.unito.chatrest.repository.MessageRepository;
import jakarta.persistence.EntityNotFoundException;
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
            //recover the chat
            chat = chatRepository.findById(request.getChatId())
                    .orElseThrow(() -> new IllegalArgumentException("Chat " + request.getChatId() + " not found"));
        } else {

            if (request.getAdoptionPostId() == null) {
                throw new IllegalArgumentException("Adoption post not found");
            }

            chat = chatRepository.findByAdopterIdAndOwnerIdAndAdoptionPostId(
                    request.getSenderId(),
                    request.getReceiverId(),
                    request.getAdoptionPostId()
            ).orElseGet(() -> {
                //create new chat
                Chat newChat = new Chat();
                newChat.setAdopterId(request.getSenderId());
                newChat.setOwnerId(request.getReceiverId());
                newChat.setAdoptionPostId(request.getAdoptionPostId());
                newChat.setRequestFlag(false);
                newChat.setAcceptedFlag(false);
                return chatRepository.save(newChat);
            });
        }

        // Create and save message
        Message message = new Message();
        message.setChat(chat);
        message.setSenderId(request.getSenderId());
        message.setReceiverId(request.getReceiverId());
        message.setMessage(request.getMessage());
        message.setTimeStamp(LocalDateTime.now());
        message.setSeen(false);

        //send email notification
        senderRabbitMQService.sendNewMessageEmail(request.getSenderId(), request.getReceiverId(), request.getMessage());

        return messageRepository.save(message);
    }


    public List<Chat> getChatsForUser(Long userId) {
        List<Chat> chats = chatRepository.findByOwnerIdOrAdopterId(userId, userId);

        // Map chats to their last message
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


    public List<Message> getChatMessagesAndMarkSeen(Long chatId, Long userId) {
        // 1. Find unread received messages
        List<Message> unreadMessages = messageRepository.findByChatIdAndReceiverIdAndSeenFalse(chatId, userId);

        // 2. Mark them as read
        for (Message m : unreadMessages) {
            m.setSeen(true);
        }

        // 3. Save update
        messageRepository.saveAll(unreadMessages);

        // 4. Return all sorted messages
        return messageRepository.findByChatIdOrderByTimeStampAsc(chatId);
    }


    public List<Message> getUnreadMessagesAndMarkSeen(Long chatId, Long userId) {
        //Find unread received messages
        List<Message> unreadMessages = messageRepository.findByChatIdAndReceiverIdAndSeenFalseOrderByTimeStampAsc(
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

        senderRabbitMQService.sendRequestEmail(chat.getOwnerId(), adopterId, "send");

        return null;
    }


    public String cancelRequest(Long chatId, Long adopterId) {
        Chat chat = chatRepository.findById(chatId).orElse(null);

        if (chat == null) {
            return "Chat non trovata";
        }

        if (!chat.getAdopterId().equals(adopterId)) {
            return "Solo l'adopter può ritirare la proposta";
        }

        if (!chat.isRequestFlag()) {
            return "Proposta non ancora inviata";
        }

        chat.setRequestFlag(false);
        chatRepository.save(chat);

        senderRabbitMQService.sendRequestEmail(chat.getOwnerId(), adopterId, "cancel");

        return null;
    }


    public String acceptRequest(Long chatId, Long ownerId) {
        try{
            Chat chat = chatRepository.findById(chatId)
                    .orElseThrow(() -> new EntityNotFoundException("Chat non trovata"));

            if (!chat.getOwnerId().equals(ownerId)) {
                return "Solo l'owner può accettare la proposta";
            }

            if (!chat.isRequestFlag()) {
                return "Proposta non ancora inviata";
            }

            chat.setAcceptedFlag(true);
            chatRepository.save(chat);

            senderRabbitMQService.sendRequestAccepted(chat.getAdoptionPostId(), chat.getAdopterId());
            senderRabbitMQService.sendAcceptEmail(chat.getAdopterId(), ownerId, "accept");

            return null;
        }catch (EntityNotFoundException e){
            return "Chat non trovata";
        }



    }


    public String rejectRequest(Long chatId, Long ownerId) {
        try{
            Chat chat = chatRepository.findById(chatId)
                    .orElseThrow(() -> new EntityNotFoundException("Chat non trovata"));

            if (!chat.getOwnerId().equals(ownerId)) {
                return "Solo l'owner può rifiutare la proposta";
            }

            if (!chat.isRequestFlag()) {
                return "Proposta non ancora inviata";
            }

            chat.setRequestFlag(false);
            chatRepository.save(chat);

            senderRabbitMQService.sendAcceptEmail(chat.getAdopterId(), ownerId, "reject");

            return null;
        }catch (EntityNotFoundException e){
            return "Chat non trovata";
        }

    }

}
