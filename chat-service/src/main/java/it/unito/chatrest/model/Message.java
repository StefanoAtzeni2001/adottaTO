package it.unito.chatrest.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "chat_id", referencedColumnName = "id")
    private Chat chat;

    private Long senderId;
    private Long receiverId;

    private String message;

    private LocalDateTime timeStamp;

    private boolean seen;
}