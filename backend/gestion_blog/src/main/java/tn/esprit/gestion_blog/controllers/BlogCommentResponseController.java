package tn.esprit.gestion_blog.controllers;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.gestion_blog.entities.BlogCommentResponse;
import tn.esprit.gestion_blog.services.IBlogCommentResponseService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("blogcommentresponses")
public class BlogCommentResponseController {

    private final IBlogCommentResponseService blogCommentResponseService;

    @PostMapping("add/{commentId}")
    public BlogCommentResponse addResponse(@PathVariable Long commentId, @RequestBody BlogCommentResponse response) {
        return blogCommentResponseService.createResponse(commentId, response);  // Pass the commentId
    }

    @PutMapping("update/{id}")
    public BlogCommentResponse updateResponse(@PathVariable Long id, @RequestBody BlogCommentResponse updatedResponse) {
        return blogCommentResponseService.updateResponse(id, updatedResponse);
    }

    @GetMapping("get/{id}")
    public BlogCommentResponse retrieveResponse(@PathVariable Long id) {
        return blogCommentResponseService.getResponseById(id);
    }

    @DeleteMapping("delete/{id}")
    public void deleteResponse(@PathVariable Long id) {
        blogCommentResponseService.deleteResponse(id);
    }

    @GetMapping("all")
    public List<BlogCommentResponse> getAllResponses() {
        return blogCommentResponseService.getAllResponses();
    }
}
