package tn.esprit.gestion_blog.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit.gestion_blog.entities.BlogPost;
import tn.esprit.gestion_blog.entities.Reaction;
import tn.esprit.gestion_blog.services.IBlogPostService;
import tn.esprit.gestion_blog.services.IBlogCommentService;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("blogposts")
public class BlogPostController {

    @Autowired
    private IBlogPostService blogPostService;
    
    @Autowired
    private IBlogCommentService blogCommentService;

    @PostMapping("add")
    public BlogPost addPost(@RequestBody BlogPost post) {
        return blogPostService.createPost(post);
    }

    @PutMapping("update/{id}")
    public BlogPost updatePost(@PathVariable Long id, @RequestBody BlogPost updatedPost) {
        return blogPostService.updatePost(id, updatedPost);
    }

    @GetMapping("get/{id}")
    public BlogPost retrievePost(@PathVariable Long id) {
        return blogPostService.getPostById(id);
    }

    @DeleteMapping("delete/{id}")
    public void deletePost(@PathVariable Long id) {
        blogPostService.deletePost(id);
    }

    @GetMapping("all")
    public List<BlogPost> getAllPosts() {
        return blogPostService.getAllPosts();
    }
    
    @GetMapping("user/{userId}")
    public List<BlogPost> getPostsByUserId(@PathVariable Long userId) {
        return blogPostService.getPostsByUserId(userId);
    }
    
    @PutMapping("{id}/reaction")
    public BlogPost updateReaction(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        BlogPost post = blogPostService.getPostById(id);
        if (payload.get("reaction") == null) {
            post.setReaction(null);
        } else if (payload.containsKey("reaction")) {
            post.setReaction(Reaction.valueOf((String) payload.get("reaction")));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid reaction type");
        }
        return blogPostService.updatePost(id, post);
    }
    
    @PutMapping("{id}/update-reaction")
    public BlogPost updateExistingReaction(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        BlogPost post = blogPostService.getPostById(id);
        if (payload.get("newReaction") == null) {
            post.setReaction(null);
        } else if (payload.containsKey("newReaction")) {
            post.setReaction(Reaction.valueOf((String) payload.get("newReaction")));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid reaction type");
        }
        return blogPostService.updatePost(id, post);
    }
    
    @GetMapping("{id}/comments/count")
    public Map<String, Integer> getCommentsCount(@PathVariable Long id) {
        int count = blogCommentService.getCommentsByPostId(id).size();
        return Map.of("count", count);
    }
}
