package tn.esprit.gestion_blog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.gestion_blog.entities.BlogCommentResponse;

@Repository
public interface BlogCommentResponseRepository extends JpaRepository<BlogCommentResponse, Long> {
}
