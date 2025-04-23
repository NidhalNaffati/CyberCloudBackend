package tn.esprit.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.entity.MedecinDocument;
import tn.esprit.entity.User;

import java.util.List;

@Repository
public interface MedecinDocumentRepository extends JpaRepository<MedecinDocument, Long> {
    List<MedecinDocument> findByUser(User user);
}
