package tn.esprit.gestion_blog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.gestion_blog.entities.BlogComment;

@Repository
public interface BlogCommentRepository extends JpaRepository<BlogComment, Long> {
}
