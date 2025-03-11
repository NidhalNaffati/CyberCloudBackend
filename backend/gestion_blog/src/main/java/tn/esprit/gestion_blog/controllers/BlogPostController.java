package tn.esprit.gestion_blog.controllers;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tn.esprit.gestion_blog.entities.BlogPost;
import tn.esprit.gestion_blog.services.IBlogPostService;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("blogposts")
public class BlogPostController {

    @Autowired
    private IBlogPostService blogPostService;

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
}
