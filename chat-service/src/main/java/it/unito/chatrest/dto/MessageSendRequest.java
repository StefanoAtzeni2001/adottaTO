package it.unito.chatrest.dto;

import lombok.*;

/**
 * DTO used to send a message.
 * Used for get unread messages, get all messages in a chat,
 * send, cancel, accept or reject an adoption request
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessageSendRequest {

    /** The ID of the chat, if it already exists */
    private Long chatId;
    private Long senderId;
    private Long receiverId;
    private String message;
    /**
     * The ID of the adoption post this chat is related to.
     * Required if this is the first message of a new conversation.
     */
    private Long adoptionPostId;
}
