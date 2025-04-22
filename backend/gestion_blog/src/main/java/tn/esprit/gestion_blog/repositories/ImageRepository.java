package tn.esprit.gestion_blog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.gestion_blog.entities.Image;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    /**
     * Trouver toutes les images associées à un post de blog
     * @param postId ID du post de blog
     * @return Liste des images associées au post
     */
    List<Image> findByBlogPost_PostId(Long postId);
    
    /**
     * Supprimer toutes les images associées à un post de blog
     * @param postId ID du post de blog
     */
    void deleteByBlogPost_PostId(Long postId);
}