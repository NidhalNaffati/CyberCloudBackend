package com.example.gestion_forum.controllers;

import com.example.gestion_forum.models.Message;
import com.example.gestion_forum.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {
    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<Message> sendMessage(@RequestBody Message message) {
        Message savedMessage = messageService.sendMessage(message);
        return ResponseEntity.ok(savedMessage);
    }

    @GetMapping("/{userId1}/{userId2}")
    public List<Message> getMessagesBetweenUsers(@PathVariable Long userId1, @PathVariable Long userId2) {
        return messageService.getMessagesBetweenUsers(userId1, userId2);
    }
}
