package com.example.gestion_forum.services;

import com.example.gestion_forum.models.Message;
import com.example.gestion_forum.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    public List<Message> getMessagesBetweenUsers(Long userId1, Long userId2) {
        return messageRepository.findBySenderIdOrReceiverId(userId1, userId2);
    }

    public Message sendMessage(Message message) {
        return messageRepository.save(message);
    }
}
