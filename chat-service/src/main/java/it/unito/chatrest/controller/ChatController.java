package it.unito.chatrest.controller;

import it.unito.chatrest.dto.ChatRequest;
import it.unito.chatrest.model.Chat;
import it.unito.chatrest.model.Message;
import it.unito.chatrest.service.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.unito.chatrest.dto.MessageSendRequest;

import java.util.List;

import static it.unito.chatrest.constants.ChatEndPoints.*;

/**
 * REST Controller responsible for managing chat-related operations between users.
 * It provides functionality for:
 * - Sending messages in new or existing chats
 * - Retrieving the list of user chats
 * - Getting chat history and unread messages
 * - Handling adoption requests through chat (send, cancel, accept, reject)
 */
@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    /**
     * Constructor for dependency injection.
     *
     * @param chatService the service responsible for handling chat-related business logic
     */
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Sends a new message in a new or an existing chat.
     *
     * @param request the message send request containing chat info and content
     * @param userId [from header] the ID of the user sending the message
     * @return ResponseEntity containing the message or an error if invalid
     */
    @PostMapping(SEND_NEW_MESSAGE)
    public ResponseEntity<?> sendMessage(@RequestBody MessageSendRequest request, @RequestHeader("User-Id") Long userId) {
        try {
            Message message = chatService.sendMessage(request, userId);
            return ResponseEntity.ok(message);
        } catch (IllegalArgumentException e) {
            String errorMessage = e.getMessage();
            return switch (errorMessage) {
                case "No sender match" ->
                    // 403 Forbidden
                        ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage);
                case "Chat not found" ->
                    // 404 Not Found
                        ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
                //case "Adoption post not found":
                default ->
                    // 400 Bad Request
                        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            };
        }
    }

    /**
     * Retrieves the list of chats for the specified user.
     *
     * @param userId [from header] the ID of the user whose chats are requested
     * @return ResponseEntity containing the list of chats
     */
    @PostMapping(GET_CHATS)
    public ResponseEntity<List<Chat>> getChatsForUser(@RequestHeader("User-Id") Long userId) {
        List<Chat> chats = chatService.getChatsForUser(userId);
        return ResponseEntity.ok(chats);
    }

    /**
     * Retrieves the full message history of a specific chat and marks messages as seen.
     *
     * @param request the chat request containing the chat ID
     * @param userId [from header] the ID of the user requesting the messages
     * @return ResponseEntity containing the list of all messages in the chat
     */
    @PostMapping(GET_HISTORY)
    public ResponseEntity<List<Message>> getFullChat(@RequestBody ChatRequest request, @RequestHeader("User-Id") Long userId) {
        List<Message> messages = chatService.getChatMessagesAndMarkSeen(request.getChatId(), userId);
        return ResponseEntity.ok(messages);
    }

    /**
     * Retrieves only unread messages from a specific chat and marks them as seen.
     *
     * @param request the chat request containing the chat ID
     * @param userId [from header] the ID of the user requesting the messages
     * @return ResponseEntity containing the list of unread messages
     */
    @PostMapping(GET_UNREAD_MESSAGES)
    public ResponseEntity<List<Message>> getUnreadMessages(@RequestBody ChatRequest request, @RequestHeader("User-Id") Long userId) {
        List<Message> unreadMessages = chatService.getUnreadMessagesAndMarkSeen(request.getChatId(), userId);
        return ResponseEntity.ok(unreadMessages);
    }

    /**
     * Sends an adoption request related to a specific chat and adoption post.
     *
     * @param request the chat request containing the chat ID
     * @param userId the ID of the user sending the request (from request header)
     * @return ResponseEntity with success or error message
     */
    @PostMapping(SEND_ADOPTION_REQUEST)
    public ResponseEntity<String> sendRequest(@RequestBody ChatRequest request, @RequestHeader("User-Id") Long userId) {
        try{
            chatService.sendRequest(request.getChatId(), userId);
            return ResponseEntity.ok("Success: request sent");
        } catch (IllegalArgumentException e) {
            String errorMessage = e.getMessage();
            return switch (errorMessage) {
                case "Chat not found" ->
                    // 404 Not Found
                        ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
                case "Only adopter can send request" ->
                    // 403 Forbidden
                        ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage);
                default ->
                    // 400 Bad Request
                        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            };
        }
    }

    /**
     * Cancels a previously sent adoption request.
     *
     * @param request the chat request containing the chat ID
     * @param userId the ID of the user cancelling the request (from request header)
     * @return ResponseEntity with success or error message
     */
    @PostMapping(DELETE_ADOPTION_REQUEST)
    public ResponseEntity<String> cancelRequest(@RequestBody ChatRequest request, @RequestHeader("User-Id") Long userId) {
        try{
            chatService.cancelRequest(request.getChatId(), userId);
            return ResponseEntity.ok("Success: request cancelled");
        }  catch (IllegalArgumentException e) {
            String errorMessage = e.getMessage();
            return switch (errorMessage) {
                case "Chat not found" ->
                    // 404 Not Found
                        ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
                case "Only adopter can cancel request" ->
                    // 403 Forbidden
                        ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage);
                case "Request not yet sent" ->
                    // 405 Method Not Allowed
                        ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorMessage);
                default ->
                    // 400 Bad Request
                        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            };
        }
    }

    /**
     * Accepts an incoming adoption request.
     *
     * @param request the chat request containing the chat ID
     * @param userId the ID of the user accepting the request (from request header)
     * @return ResponseEntity with success or error message
     */
    @PostMapping(ACCEPT_ADOPTION_REQUEST)
    public ResponseEntity<String> acceptRequest(@RequestBody ChatRequest request, @RequestHeader("User-Id") Long userId) {
        try{
            chatService.acceptRequest(request.getChatId(), userId);
            return ResponseEntity.ok("Success: request accepted");
        }   catch (IllegalArgumentException e) {
            String errorMessage = e.getMessage();
            return switch (errorMessage) {
                case "Chat not found" ->
                    // 404 Not Found
                        ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
                case "Only owner can accept request" ->
                    // 403 Forbidden
                        ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage);
                case "Request not yet sent" ->
                    // 405 Method Not Allowed
                        ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorMessage);
                default ->
                    // 400 Bad Request
                        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            };
        }
    }

    /**
     * Rejects an incoming adoption request.
     *
     * @param request the chat request containing the chat ID
     * @param userId the ID of the user rejecting the request (from request header)
     * @return ResponseEntity with success or error message
     */
    @PostMapping(REJECT_ADOPTION_REQUEST)
    public ResponseEntity<String> rejectRequest(@RequestBody ChatRequest request, @RequestHeader("User-Id") Long userId) {
        try{
            chatService.rejectRequest(request.getChatId(), userId);
            return ResponseEntity.ok("Success: request rejected");
        } catch (IllegalArgumentException e) {
            String errorMessage = e.getMessage();
            return switch (errorMessage) {
                case "Chat not found" ->
                    // 404 Not Found
                        ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
                case "Only owner can reject request" ->
                    // 403 Forbidden
                        ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorMessage);
                case "Request not yet sent" ->
                    // 405 Method Not Allowed
                        ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(errorMessage);
                default ->
                    // 400 Bad Request
                        ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            };
        }
    }
}


