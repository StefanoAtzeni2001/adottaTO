package it.unito.chatrest.repository;

import it.unito.chatrest.model.Chat;
import it.unito.chatrest.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findByOwnerIdOrAdopterId(Long ownerId, Long adopterId);
}
