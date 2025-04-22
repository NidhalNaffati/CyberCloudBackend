package tn.esprit.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Complaint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int complaintId;
    private String subject;  // remplacé consultationId par subject

    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id" , nullable = false)

    User user;


    private int starRatingConsultation;
    private boolean isUrgent;
    private boolean isRead;
    private LocalDateTime date; // nouveau champ

    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ResponseComplaint> responses;

    // Constructeurs
    public Complaint() {}

    // Getters et Setters
    public int getComplaintId() { return complaintId; }
    public void setComplaintId(int complaintId) { this.complaintId = complaintId; }
    public String getSubject() { return subject; }  // nouveau getter
    public void setSubject(String subject) { this.subject = subject; }  // nouveau setter
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

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

    public List<ResponseComplaint> getResponses() { return responses; }
    public void setResponses(List<ResponseComplaint> responses) { this.responses = responses; }
}