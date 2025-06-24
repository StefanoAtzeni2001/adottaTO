package it.unito.chatrest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO used to transfer chatId.
 * Used for get unread messages, get all messages in a chat,
 * send, cancel, accept or reject an adoption request
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatRequest {
    private Long chatId;

}
