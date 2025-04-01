package tn.esprit.gestion_reclamation.repositories;

import tn.esprit.gestion_reclamation.entities.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IComplaintRepository extends JpaRepository<Complaint, Integer> {
    List<Complaint> findByUserId(int userId);
}
