package tn.esprit.gestion_blog.services;

import tn.esprit.gestion_blog.entities.BlogComment;

import java.util.List;

public interface IBlogCommentService {
    List<BlogComment> getAllComments();
    List<BlogComment> getCommentsByPostId(Long postId);  // Récupérer tous les commentaires
    BlogComment getCommentById(Long id);  // Retourner un commentaire par son ID
    BlogComment createComment(Long postId, BlogComment comment);  // Créer un commentaire en s'assurant que le BlogPost existe
    BlogComment updateComment(Long id, BlogComment updatedComment);  // Mettre à jour un commentaire existant
    void deleteComment(Long id);  // Supprimer un commentaire
    
    // Nouvelle méthode pour récupérer les commentaires d'un utilisateur spécifique
    List<BlogComment> getCommentsByUserId(Long userId);
}
