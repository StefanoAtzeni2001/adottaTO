package it.unito.chatrest.service;

import it.unito.chatrest.dto.MessageSendRequest;
import it.unito.chatrest.model.Chat;
import it.unito.chatrest.model.Message;
import it.unito.chatrest.repository.ChatRepository;
import it.unito.chatrest.repository.MessageRepository;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class responsible for managing chat interactions between users,
 * including message exchange, chat creation, request handling (send, cancel, accept, reject),
 * and marking messages as seen.
 */
@Service
public class ChatService {
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;
    private final SenderRabbitMQService senderRabbitMQService;

    /**
     * Constructs a new ChatService with the required dependencies.
     *
     * @param chatRepository          repository for managing chat data
     * @param messageRepository       repository for managing message data
     * @param senderRabbitMQService   service for sending events and notifications via RabbitMQ
     */
    public ChatService(ChatRepository chatRepository,
                         MessageRepository messageRepository,
                         SenderRabbitMQService senderRabbitMQService) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
        this.senderRabbitMQService = senderRabbitMQService;
    }

    /**
     * Sends a new message in an existing or new chat and notifying the receiver
     *
     * @param request the message send request
     * @param userId  the ID of the user performing the operation
     * @return the saved {@link Message} entity
     * @throws IllegalArgumentException if the sender does not match the user or if the chat or adoption post is not found
     */
    public Message sendMessage(MessageSendRequest request, Long userId) {

        Chat chat;

        if(!request.getSenderId().equals(userId)) {
            throw new IllegalArgumentException("No sender match"); //errore 403
        }

        if (request.getChatId() != null) {
            //recover the chat
            chat = chatRepository.findById(request.getChatId())
                    .orElseThrow(() -> new IllegalArgumentException("Chat not found")); //404
        } else {

            if (request.getAdoptionPostId() == null) {
                throw new IllegalArgumentException("Adoption post not found"); //400
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
        senderRabbitMQService.sendNewMessageEmail(request.getReceiverId(), request.getSenderId(), request.getMessage());

        System.out.println("Inviata notifica email:"
                + "\nSenderId: " + request.getSenderId()
                + "\nReceiverId: " + request.getReceiverId()
                + "\nMessage: " + request.getMessage()
        );

        return messageRepository.save(message);
    }

    /**
     * Retrieves all chats involving the given user and sorts them by most recent message
     *
     * @param userId the ID of the user
     * @return a list of {@link Chat} objects sorted by last activity
     */
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

    /**
     * Retrieves all messages in a chat and marks all unread messages received by the user as seen
     *
     * @param chatId the ID of the chat
     * @param userId the ID of the user
     * @return list of {@link Message} ordered by timestamp ascending
     */
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

    /**
     * Retrieves all unread messages in a chat received by the user and marks them as seen
     *
     * @param chatId the ID of the chat
     * @param userId the ID of the receiver
     * @return list of unread {@link Message} objects ordered by timestamp ascending
     */
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

    /**
     * Sends an adoption request by setting a flag on the chat and notifying the receiver
     *
     * @param chatId    the ID of the chat
     * @param adopterId the ID of the adopter sending the request
     * @throws IllegalArgumentException if chat not found or user is not the adopter
     */
    public void sendRequest(Long chatId, Long adopterId) {

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found")); // 404

        if (!chat.getAdopterId().equals(adopterId)) {
            throw new IllegalArgumentException("Only adopter can send request"); // 403
        }

        chat.setRequestFlag(true);
        chatRepository.save(chat);

        senderRabbitMQService.sendRequestEmail(chat.getOwnerId(), adopterId, "send");

        System.out.println("Inviata notifica email:"
                + "\nOwner (SenderId): " + chat.getOwnerId()
                + "\nAdopter (ReceiverId): " + adopterId
                + "\nMessage: " + "send"
        );

    }

    /**
     * Cancels a previously sent adoption request.
     *
     * @param chatId    the ID of the chat
     * @param adopterId the ID of the adopter
     * @throws IllegalArgumentException if chat not found, user is not the adopter, or request not sent yet
     */
    public void cancelRequest(Long chatId, Long adopterId) {

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found")); // 404

        if (!chat.getAdopterId().equals(adopterId)) {
            throw new IllegalArgumentException("Only adopter can cancel request"); // 403
        }

        if (!chat.isRequestFlag()) {
            throw new IllegalArgumentException("Request not yet sent"); // 405
        }

        chat.setRequestFlag(false);
        chatRepository.save(chat);

        senderRabbitMQService.sendRequestEmail(chat.getOwnerId(), adopterId, "cancel");

        System.out.println("Inviata notifica email:"
                + "\nOwner (SenderId): " + chat.getOwnerId()
                + "\nAdopter (ReceiverId): " + adopterId
                + "\nMessage: " + "cancel"
        );

    }

    /**
     * Accepts a received adoption request.
     *
     * @param chatId  the ID of the chat
     * @param ownerId the ID of the owner
     * @throws IllegalArgumentException if chat not found, user is not the owner, or request not sent yet
     */
    public void acceptRequest(Long chatId, Long ownerId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found")); // 404

        if (!chat.getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("Only owner can accept request"); // 403
        }

        if (!chat.isRequestFlag()) {
            throw new IllegalArgumentException("Request not yet sent"); // 405
        }

        chat.setAcceptedFlag(true);
        chatRepository.save(chat);

        senderRabbitMQService.sendRequestAccepted(chat.getAdoptionPostId(), chat.getAdopterId());
        senderRabbitMQService.sendAcceptEmail(chat.getAdopterId(), ownerId, "accept");

        System.out.println("Inviata notifica email:"
                + "\nAdopter (SenderId): " + chat.getAdopterId()
                + "\nOwner (ReceiverId): " + ownerId
                + "\nMessage: " + "accept"
        );

    }

    /**
     * Rejects a received adoption request.
     *
     * @param chatId  the ID of the chat
     * @param ownerId the ID of the owner
     * @throws IllegalArgumentException if chat not found, user is not the owner, or request not sent yet
     */
    public void rejectRequest(Long chatId, Long ownerId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found")); // 404

        if (!chat.getOwnerId().equals(ownerId)) {
            throw new IllegalArgumentException("Only owner can reject request"); // 403
        }

        if (!chat.isRequestFlag()) {
            throw new IllegalArgumentException("Request not yet sent"); // 405
        }

        chat.setRequestFlag(false);
        chatRepository.save(chat);

        senderRabbitMQService.sendAcceptEmail(chat.getAdopterId(), ownerId, "reject");

        System.out.println("Inviata notifica email:"
                + "\nAdopter (SenderId): " + chat.getAdopterId()
                + "\nOwner (ReceiverId): " + ownerId
                + "\nMessage: " + "reject"
        );
    }

}
