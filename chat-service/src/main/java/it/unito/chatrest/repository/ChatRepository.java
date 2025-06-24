package it.unito.chatrest.repository;

import it.unito.chatrest.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    List<Chat> findByOwnerIdOrAdopterId(Long ownerId, Long adopterId);

    Optional<Chat> findByAdopterIdAndOwnerIdAndAdoptionPostId(Long adopterId, Long ownerId, Long adoptionPostId);

}
