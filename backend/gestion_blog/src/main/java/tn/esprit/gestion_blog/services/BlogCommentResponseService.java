package tn.esprit.gestion_blog.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.gestion_blog.entities.BlogComment;
import tn.esprit.gestion_blog.entities.BlogCommentResponse;
import tn.esprit.gestion_blog.repositories.BlogCommentRepository;
import tn.esprit.gestion_blog.repositories.BlogCommentResponseRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogCommentResponseService implements IBlogCommentResponseService {

    private final BlogCommentResponseRepository blogCommentResponseRepository;
    private final BlogCommentRepository blogCommentRepository;

    @Override
    public List<BlogCommentResponse> getAllResponses() {
        return blogCommentResponseRepository.findAll();
    }

    @Override
    public BlogCommentResponse getResponseById(Long id) {
        return blogCommentResponseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Response not found with id: " + id));
    }

    @Override
    public BlogCommentResponse createResponse(Long commentId, BlogCommentResponse response) {
        BlogComment blogComment = blogCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("BlogComment not found with id: " + commentId));

        response.setBlogComment(blogComment);  // Lier la réponse au commentaire spécifique
        return blogCommentResponseRepository.save(response);
    }

    @Override
    public BlogCommentResponse updateResponse(Long id, BlogCommentResponse updatedResponse) {
        return blogCommentResponseRepository.findById(id)
                .map(response -> {
                    response.setContent(updatedResponse.getContent());
                    return blogCommentResponseRepository.save(response);
                })
                .orElseThrow(() -> new RuntimeException("Response not found with id: " + id));
    }

    @Override
    public void deleteResponse(Long id) {
        blogCommentResponseRepository.deleteById(id);
    }
    
    @Override
    public List<BlogCommentResponse> getResponsesByCommentId(Long commentId) {
        return blogCommentResponseRepository.findByBlogComment_CommentId(commentId);
    }
    
    @Override
    public List<BlogCommentResponse> getResponsesByUserId(Long userId) {
        return blogCommentResponseRepository.findByUserId(userId);
    }
}
