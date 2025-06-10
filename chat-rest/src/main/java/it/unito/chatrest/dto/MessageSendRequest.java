package it.unito.chatrest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class MessageSendRequest {
    private Long chatId;           // opzionale
    private Long senderId;
    private Long receiverId;
    private String message;
    private Long adoptionPostId;
}
