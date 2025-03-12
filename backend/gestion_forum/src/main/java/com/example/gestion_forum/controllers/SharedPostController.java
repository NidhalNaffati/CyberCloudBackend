package com.example.gestion_forum.controllers;

import com.example.gestion_forum.dtos.SharedPostDTO;
import com.example.gestion_forum.models.SharedPost;
import com.example.gestion_forum.services.SharedPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shared-posts")
@RequiredArgsConstructor
public class SharedPostController {
    private final SharedPostService sharedPostService;

    @PostMapping
    public ResponseEntity<SharedPost> sharePost(@RequestBody SharedPostDTO sharedPostDTO) {
        SharedPost savedSharedPost = sharedPostService.sharePost(sharedPostDTO);
        return ResponseEntity.ok(savedSharedPost);
    }

    @GetMapping("/{userId}")
    public List<SharedPost> getUserSharedPosts(@PathVariable Long userId) {
        return sharedPostService.getUserSharedPosts(userId);
    }
}
