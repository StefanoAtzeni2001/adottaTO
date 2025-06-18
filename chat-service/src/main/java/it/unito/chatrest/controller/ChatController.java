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
    public ResponseEntity<?> sendMessage(@RequestBody MessageSendRequest request) {
        try {
            Message message = chatService.sendMessage(request);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/chats")
    public ResponseEntity<List<Chat>> getChatsForUser(@RequestBody UserIdRequest request) {
        List<Chat> chats = chatService.getChatsForUser(request.getUserId());
        return ResponseEntity.ok(chats);
    }

    @PostMapping("/history")
    public ResponseEntity<List<Message>> getFullChat(@RequestBody ChatRequest request) {
        List<Message> messages = chatService.getChatMessagesAndMarkSeen(request.getChatId(), request.getUserId());
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/unread")
    public ResponseEntity<List<Message>> getUnreadMessages(@RequestBody ChatRequest request) {
        List<Message> unreadMessages = chatService.getUnreadMessagesAndMarkSeen(request.getChatId(), request.getUserId());
        return ResponseEntity.ok(unreadMessages);
    }

    @PostMapping("/sendRequest")
    public ResponseEntity<String> sendRequest(@RequestBody ChatRequest request) {
        String result = chatService.sendRequest(request.getChatId(), request.getUserId());

        if (result.equals("Proposta inviata con successo")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/cancelRequest")
    public ResponseEntity<String> cancelRequest(@RequestBody ChatRequest request) {
        String result = chatService.cancelRequest(request.getChatId(), request.getUserId());

        if (result.equals("Proposta ritirata con successo")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/acceptRequest")
    public ResponseEntity<String> acceptRequest(@RequestBody ChatRequest request) {
        String result = chatService.acceptRequest(request.getChatId(), request.getUserId());

        if (result.equals("Richiesta accettata con successo")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    @PostMapping("/rejectRequest")
    public ResponseEntity<String> rejectRequest(@RequestBody ChatRequest request) {
        String result = chatService.rejectRequest(request.getChatId(), request.getUserId());

        if (result.equals("Richiesta rifiutata con successo")) {
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }
}


