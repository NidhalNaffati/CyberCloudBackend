package tn.esprit.gestion_blog.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.gestion_blog.entities.BlogComment;
import tn.esprit.gestion_blog.services.IBlogCommentService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("blogcomments")
public class BlogCommentController {

    private final IBlogCommentService blogCommentService;

    @PostMapping("add/{postId}")
    public BlogComment addComment(@PathVariable Long postId, @RequestBody BlogComment comment) {
        return blogCommentService.createComment(postId, comment);  // On passe postId pour associer le commentaire au BlogPost
    }

    @PutMapping("update/{id}")
    public BlogComment updateComment(@PathVariable Long id, @RequestBody BlogComment updatedComment) {
        return blogCommentService.updateComment(id, updatedComment);
    }

    @GetMapping("get/{id}")
    public BlogComment retrieveComment(@PathVariable Long id) {
        return blogCommentService.getCommentById(id);
    }

    @DeleteMapping("delete/{id}")
    public void deleteComment(@PathVariable Long id) {
        blogCommentService.deleteComment(id);
    }

    @GetMapping("all")
    public List<BlogComment> getAllComments() {
        return blogCommentService.getAllComments();
    }
}
