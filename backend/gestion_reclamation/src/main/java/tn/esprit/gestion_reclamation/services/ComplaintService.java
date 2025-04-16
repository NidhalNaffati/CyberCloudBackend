package tn.esprit.gestion_reclamation.services;
import java.time.LocalDateTime;

import jakarta.transaction.Transactional;
import tn.esprit.gestion_reclamation.entities.Complaint;
import tn.esprit.gestion_reclamation.repositories.IComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ComplaintService {
    @Autowired
    private IComplaintRepository complaintRepository;

    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public Complaint addComplaint(Complaint complaint) {
        complaint.setDate(LocalDateTime.now());
        return complaintRepository.save(complaint);
    }

    // ✅ Modifier une réclamation par ID
    public Complaint updateComplaintById(int complaintId, Complaint updatedComplaint) {
        return complaintRepository.findById(complaintId).map(existingComplaint -> {
            existingComplaint.setConsultationId(updatedComplaint.getConsultationId());
            existingComplaint.setContent(updatedComplaint.getContent());
            existingComplaint.setUserId(updatedComplaint.getUserId());
            existingComplaint.setStarRatingConsultation(updatedComplaint.getStarRatingConsultation());
            existingComplaint.setUrgent(updatedComplaint.isUrgent());
            existingComplaint.setRead(updatedComplaint.isRead());
            existingComplaint.setConsultationId(updatedComplaint.getConsultationId());
            existingComplaint.setDate(LocalDateTime.now()); // 🔄 nouveau champ


            return complaintRepository.save(existingComplaint);
        }).orElse(null); // Retourne `null` si l'ID n'existe pas
    }

    public void deleteComplaint(int complaintId) {
        complaintRepository.deleteById(complaintId);
    }

    public Complaint getComplaintById(int complaintId) {
        return complaintRepository.findById(complaintId).orElse(null);
    }
    public List<Complaint> getComplaintsByUser(int userId) {
        return complaintRepository.findByUserId(userId);
    }
    public List<Complaint> getUnreadComplaintsOrderedByDateDesc() {
        return complaintRepository.findByIsReadFalseOrderByDateDesc();
    }
    // ComplaintService.java

    public Complaint markComplaintAsRead(int complaintId) {
        return complaintRepository.findById(complaintId).map(existingComplaint -> {
            existingComplaint.setRead(true);  // Met à jour le champ isRead
            return complaintRepository.save(existingComplaint);
        }).orElse(null); // Retourne null si la réclamation n'existe pas
    }


}







