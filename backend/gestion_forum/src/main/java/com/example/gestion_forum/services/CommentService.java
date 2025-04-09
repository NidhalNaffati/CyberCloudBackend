package com.example.gestion_forum.services;

import com.example.gestion_forum.models.Comment;
import com.example.gestion_forum.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {
    private final CommentRepository commentRepository;

    @Autowired
    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    // ✅ Get all comments
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    // ✅ Create a new comment
    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    // ✅ Update an existing comment
    public Comment updateComment(Long id, Comment commentDetails) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        if (optionalComment.isPresent()) {
            Comment existingComment = optionalComment.get();
            existingComment.setContent(commentDetails.getContent());
            existingComment.setUpdatedAt(commentDetails.getUpdatedAt());
            return commentRepository.save(existingComment);
        }
        throw new RuntimeException("Comment not found with id " + id);
    }

    // ✅ Delete a comment
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}
