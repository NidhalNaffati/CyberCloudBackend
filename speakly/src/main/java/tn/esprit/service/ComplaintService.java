package tn.esprit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.entity.Complaint;
import tn.esprit.entity.User;
import tn.esprit.exception.ResourceNotFoundException;
import tn.esprit.repository.IComplaintRepository;
import tn.esprit.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ComplaintService {
    @Autowired
    private IComplaintRepository complaintRepository;
    @Autowired
    private UserRepository userRepository;
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    public Complaint addComplaint(Complaint complaint, Long user_id) throws Exception {
        // Trouver l'utilisateur existant
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new Exception("Utilisateur non trouvé avec l'ID: " + user_id));

        // Associer l'utilisateur à la plainte
        complaint.setUser(user);
        complaint.setDate(LocalDateTime.now());
        complaint.setRead(false); // Par défaut non lu

        return complaintRepository.save(complaint);
    }

    // ✅ Modifier une réclamation par ID
    public Complaint updateComplaintById(int complaintId, Complaint updatedComplaint) {
        return complaintRepository.findById(complaintId)
                .map(existingComplaint -> {
                    // Conservez l'utilisateur original
                    User originalUser = existingComplaint.getUser();

                    // Mettez à jour uniquement les champs modifiables
                    existingComplaint.setSubject(updatedComplaint.getSubject());
                    existingComplaint.setContent(updatedComplaint.getContent());
                    existingComplaint.setStarRatingConsultation(updatedComplaint.getStarRatingConsultation());
                    existingComplaint.setUrgent(updatedComplaint.isUrgent());
                    existingComplaint.setRead(updatedComplaint.isRead());
                    existingComplaint.setDate(LocalDateTime.now());

                    // Réassociez l'utilisateur original
                    existingComplaint.setUser(originalUser);

                    return complaintRepository.save(existingComplaint);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with id: " + complaintId));
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

    public Complaint markComplaintAsRead(int complaintId) {
        return complaintRepository.findById(complaintId).map(existingComplaint -> {
            existingComplaint.setRead(true);
            return complaintRepository.save(existingComplaint);
        }).orElse(null);
    }
}