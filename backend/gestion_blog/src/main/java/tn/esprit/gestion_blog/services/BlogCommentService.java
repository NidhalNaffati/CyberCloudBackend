package tn.esprit.gestion_blog.services;

import tn.esprit.gestion_blog.entities.BlogComment;
import tn.esprit.gestion_blog.entities.BlogPost;
import tn.esprit.gestion_blog.repositories.BlogCommentRepository;
import tn.esprit.gestion_blog.repositories.BlogPostRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class BlogCommentService implements IBlogCommentService {

    private final BlogCommentRepository blogCommentRepository;
    private final BlogPostRepository blogPostRepository;

    @Override
    public List<BlogComment> getAllComments() {
        return blogCommentRepository.findAll();
    }

    @Override
    public BlogComment getCommentById(Long id) {
        return blogCommentRepository.findById(id).orElse(null);
    }

    @Override
    public List<BlogComment> getCommentsByPostId(Long postId) {
        return blogCommentRepository.findByBlogPost_PostId(postId);  // ✅ Correction ici
    }

    @Override
    public BlogComment createComment(Long postId, BlogComment comment) {
        BlogPost blogPost = blogPostRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("BlogPost not found"));
        comment.setBlogPost(blogPost);  // Lier le commentaire au BlogPost
        return blogCommentRepository.save(comment);
    }

    @Override
    public BlogComment updateComment(Long id, BlogComment updatedComment) {
        BlogComment existingComment = blogCommentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        existingComment.setContent(updatedComment.getContent());
        return blogCommentRepository.save(existingComment);
    }

    @Override
    public void deleteComment(Long id) {
        BlogComment comment = blogCommentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        blogCommentRepository.delete(comment);
    }
    
    @Override
    public List<BlogComment> getCommentsByUserId(Long userId) {
        return blogCommentRepository.findByUserId(userId);
    }
}