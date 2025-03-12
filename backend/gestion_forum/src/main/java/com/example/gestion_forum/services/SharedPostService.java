package com.example.gestion_forum.services;

import com.example.gestion_forum.dtos.SharedPostDTO;
import com.example.gestion_forum.models.Post;
import com.example.gestion_forum.models.SharedPost;
import com.example.gestion_forum.models.User;
import com.example.gestion_forum.repository.PostRepository;
import com.example.gestion_forum.repository.SharedPostRepository;
import com.example.gestion_forum.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SharedPostService {
    private final SharedPostRepository sharedPostRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public SharedPost sharePost(SharedPostDTO sharedPostDTO) {
        Optional<Post> post = postRepository.findById(sharedPostDTO.getPostId());
        Optional<User> user = userRepository.findById(sharedPostDTO.getUserId());

        if (post.isPresent() && user.isPresent()) {
            SharedPost sharedPost = new SharedPost();
            sharedPost.setPost(post.get());
            sharedPost.setUser(user.get());
            sharedPost.setSharedAt(LocalDateTime.now());

            return sharedPostRepository.save(sharedPost);
        } else {
            throw new RuntimeException("Invalid post ID or user ID");
        }
    }

    public List<SharedPost> getUserSharedPosts(Long userId) {
        return sharedPostRepository.findByUserId(userId);
    }
}
