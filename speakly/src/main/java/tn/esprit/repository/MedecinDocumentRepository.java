package tn.esprit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.entity.MedecinDocument;
import tn.esprit.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedecinDocumentRepository extends JpaRepository<MedecinDocument, Long> {

    @Query("SELECT md FROM MedecinDocument md WHERE md.user.id = :userId")
    Optional<MedecinDocument> findByUserId(@Param("userId") Long userId);
}
