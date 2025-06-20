package it.unito.chatrest.controller;

import it.unito.chatrest.dto.ChatRequest;
import it.unito.chatrest.dto.UserIdRequest;
import it.unito.chatrest.model.Chat;
import it.unito.chatrest.model.Message;
import it.unito.chatrest.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.unito.chatrest.dto.MessageSendRequest;

import java.util.List;


@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody MessageSendRequest request, @RequestHeader("User-Id") Long userId) {
        try {
            Message message = chatService.sendMessage(request, userId);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/chats")
    public ResponseEntity<List<Chat>> getChatsForUser(@RequestHeader("User-Id") Long userId) {
        List<Chat> chats = chatService.getChatsForUser(userId);
        return ResponseEntity.ok(chats);
    }

    @PostMapping("/history")
    public ResponseEntity<List<Message>> getFullChat(@RequestBody ChatRequest request, @RequestHeader("User-Id") Long userId) {
        List<Message> messages = chatService.getChatMessagesAndMarkSeen(request.getChatId(), userId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/unread")
    public ResponseEntity<List<Message>> getUnreadMessages(@RequestBody ChatRequest request, @RequestHeader("User-Id") Long userId) {
        List<Message> unreadMessages = chatService.getUnreadMessagesAndMarkSeen(request.getChatId(), userId);
        return ResponseEntity.ok(unreadMessages);
    }

    @PostMapping("/sendRequest")
    public ResponseEntity<String> sendRequest(@RequestBody ChatRequest request, @RequestHeader("User-Id") Long userId) {
        String result = chatService.sendRequest(request.getChatId(), userId);

        if (result == null) {
            return ResponseEntity.ok("Proposta inviata con successo");
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/cancelRequest")
    public ResponseEntity<String> cancelRequest(@RequestBody ChatRequest request, @RequestHeader("User-Id") Long userId) {
        String result = chatService.cancelRequest(request.getChatId(), userId);

        if (result == null) {
            return ResponseEntity.ok("Proposta cancellata con successo");
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/acceptRequest")
    public ResponseEntity<String> acceptRequest(@RequestBody ChatRequest request, @RequestHeader("User-Id") Long userId) {
        String result = chatService.acceptRequest(request.getChatId(), userId);

        if (result == null) {
            return ResponseEntity.ok("Richiesta accettata con successo");
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/rejectRequest")
    public ResponseEntity<String> rejectRequest(@RequestBody ChatRequest request, @RequestHeader("User-Id") Long userId) {
        String result = chatService.rejectRequest(request.getChatId(), userId);

        if (result == null) {
            return ResponseEntity.ok("Richiesta rifiutata con successo");
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}


