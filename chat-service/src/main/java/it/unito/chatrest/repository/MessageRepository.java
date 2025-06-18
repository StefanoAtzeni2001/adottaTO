package it.unito.chatrest.repository;

import java.util.List;
import it.unito.chatrest.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    Message findFirstByChatIdOrderByTimeStampDesc(Long chatId);

    List<Message> findByChatIdAndReceiverIdAndSeenFalse(Long chatId, Long receiverId);

    List<Message> findByChatIdOrderByTimeStampAsc(Long chatId);



}
