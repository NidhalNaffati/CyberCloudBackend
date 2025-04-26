package tn.esprit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit.entity.Complaint;
import tn.esprit.entity.ResponseComplaint;

import java.util.List;
import java.util.Optional;

@Repository
public interface IResponseComplaintRepository extends JpaRepository<ResponseComplaint, Integer> {
    List<ResponseComplaint> findByComplaint_ComplaintId(int complaintId);
    List<ResponseComplaint> findByIsReadRepFalseOrderByDateRepDesc();
    @Query("SELECT r.complaint FROM ResponseComplaint r WHERE r.responseId = :responseId")
    Optional<Complaint> findComplaintByResponseId(@Param("responseId") int responseId);
}
