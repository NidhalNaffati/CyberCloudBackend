package com.example.gestion_forum.controllers;

import com.example.gestion_forum.models.Reaction;
import com.example.gestion_forum.services.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reactions")
@RequiredArgsConstructor
public class ReactionController {
    private final ReactionService reactionService;

    @GetMapping("/{postId}")
    public List<Reaction> getReactions(@PathVariable Long postId) {
        return reactionService.getReactionsForPost(postId);
    }

    @PostMapping
    public ResponseEntity<Reaction> addReaction(@RequestBody Reaction reaction) {
        Reaction savedReaction = reactionService.addReaction(reaction);
        return ResponseEntity.ok(savedReaction);
    }
}
