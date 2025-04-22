package tn.esprit.gestion_blog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.gestion_blog.entities.BlogPost;
import tn.esprit.gestion_blog.exceptions.ResourceNotFoundException;
import tn.esprit.gestion_blog.repositories.BlogPostRepository;
import tn.esprit.gestion_blog.services.IBlogPostService;

import java.util.List;

@Service
public class BlogPostService implements IBlogPostService {

    @Autowired
    private BlogPostRepository blogPostRepository;

    @Override
    public List<BlogPost> getAllPosts() {
        return blogPostRepository.findAll();
    }

    @Override
    public BlogPost getPostById(Long id) {
        return blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BlogPost not found with id: " + id));
    }

    @Override
    public BlogPost createPost(BlogPost post) {
        return blogPostRepository.save(post);
    }

    @Override
    public BlogPost updatePost(Long id, BlogPost updatedPost) {
        BlogPost existingPost = getPostById(id);
        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setContent(updatedPost.getContent());
        existingPost.setReaction(updatedPost.getReaction());
        return blogPostRepository.save(existingPost);
    }

    @Override
    public void deletePost(Long id) {
        BlogPost post = getPostById(id);
        blogPostRepository.delete(post);
    }
    
    @Override
    public List<BlogPost> getPostsByUserId(Long userId) {
        return blogPostRepository.findByUserId(userId);
    }
}
