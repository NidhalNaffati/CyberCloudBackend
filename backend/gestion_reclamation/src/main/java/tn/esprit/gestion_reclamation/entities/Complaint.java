package tn.esprit.gestion_reclamation.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.util.List;
import java.time.LocalDateTime;

@Entity
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int complaintId;
    private int consultationId;

    private String content;
    private int userId;
    private int starRatingConsultation;
    private boolean isUrgent;
    private boolean isRead;
    private LocalDateTime date; // nouveau champ

    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ResponseComplaint> responses;

    // Constructeurs
    public Complaint() {}

    // Getters et Setters
    public int getComplaintId() { return complaintId; }
    public void setComplaintId(int complaintId) { this.complaintId = complaintId; }
    public int getConsultationId() { return consultationId; }
    public void setConsultationId(int consultationId) { this.consultationId = consultationId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getStarRatingConsultation() { return starRatingConsultation; }
    public void setStarRatingConsultation(int starRatingConsultation) { this.starRatingConsultation = starRatingConsultation; }
    @JsonProperty("isUrgent")

    public boolean isUrgent() { return isUrgent; }
    public void setUrgent(boolean urgent) { isUrgent = urgent; }
    @JsonProperty("isRead")

    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
}






