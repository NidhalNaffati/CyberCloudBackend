package tn.esprit.gestion_blog.services;

import tn.esprit.gestion_blog.entities.BlogCommentResponse;

import java.util.List;

public interface IBlogCommentResponseService {
    List<BlogCommentResponse> getAllResponses();
    BlogCommentResponse getResponseById(Long id);
    BlogCommentResponse createResponse(Long commentId, BlogCommentResponse response);  // Modification ici pour prendre en compte commentId
    BlogCommentResponse updateResponse(Long id, BlogCommentResponse updatedResponse);
    void deleteResponse(Long id);
    
    // Nouvelles méthodes pour compléter les fonctionnalités CRUD
    List<BlogCommentResponse> getResponsesByCommentId(Long commentId);
    List<BlogCommentResponse> getResponsesByUserId(Long userId);
}
