package tn.esprit.gestion_reclamation.services;


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
        return complaintRepository.save(complaint);
    }

    // ✅ Modifier une réclamation par ID
    public Complaint updateComplaintById(int complaintId, Complaint updatedComplaint) {
        return complaintRepository.findById(complaintId).map(existingComplaint -> {
            existingComplaint.setAppointmentId(updatedComplaint.getAppointmentId());
            existingComplaint.setContent(updatedComplaint.getContent());
            existingComplaint.setUserId(updatedComplaint.getUserId());
            existingComplaint.setStarRatingAppointment(updatedComplaint.getStarRatingAppointment());
            existingComplaint.setUrgent(updatedComplaint.isUrgent());
            existingComplaint.setRead(updatedComplaint.isRead());
            return complaintRepository.save(existingComplaint);
        }).orElse(null); // Retourne `null` si l'ID n'existe pas
    }

    public void deleteComplaint(int complaintId) {
        complaintRepository.deleteById(complaintId);
    }

    public Complaint getComplaintById(int complaintId) {
        return complaintRepository.findById(complaintId).orElse(null);
    }
}


