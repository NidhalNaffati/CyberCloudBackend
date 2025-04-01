package tn.esprit.gestion_reclamation.repositories;

import tn.esprit.gestion_reclamation.entities.ResponseComplaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IResponseComplaintRepository extends JpaRepository<ResponseComplaint, Integer> {
    List<ResponseComplaint> findByComplaint_ComplaintId(int complaintId);
}
