package com.example.gestion_forum.services;

import com.example.gestion_forum.models.Reaction;
import com.example.gestion_forum.repository.ReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReactionService {
    private final ReactionRepository reactionRepository;

    public List<Reaction> getReactionsForPost(Long postId) {
        return reactionRepository.findByPostId(postId);
    }

    public Reaction addReaction(Reaction reaction) {
        return reactionRepository.save(reaction);
    }
}
