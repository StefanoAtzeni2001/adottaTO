package it.unito.chatrest.dto;

public class ChatRequest {
    private Long chatId;

    private Long userId;

    public Long getChatId() {
        return chatId;
    }

    public Long getUserId() {
        return userId;
    }
}
