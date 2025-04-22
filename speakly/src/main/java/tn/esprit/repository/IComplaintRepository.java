package tn.esprit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.entity.Complaint;

import java.util.List;

@Repository
public interface IComplaintRepository extends JpaRepository<Complaint, Integer> {
    List<Complaint> findByUserId(int userId);
    List<Complaint> findByIsReadFalseOrderByDateDesc();

}
