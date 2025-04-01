package tn.esprit.gestion_reclamation.entities;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int complaintId;

    private int appointmentId;
    private String content;
    private int userId;
    private int starRatingAppointment;
    private boolean isUrgent;
    private boolean isRead;

    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResponseComplaint> responses;

    // Constructeurs
    public Complaint() {}

    // Getters et Setters
    public int getComplaintId() { return complaintId; }
    public void setComplaintId(int complaintId) { this.complaintId = complaintId; }

    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getStarRatingAppointment() { return starRatingAppointment; }
    public void setStarRatingAppointment(int starRatingAppointment) { this.starRatingAppointment = starRatingAppointment; }

    public boolean isUrgent() { return isUrgent; }
    public void setUrgent(boolean urgent) { isUrgent = urgent; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
