package tn.esprit.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class ResponseComplaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int responseId;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id" , nullable = false)

    User user;

    private String content;
    private boolean isReadRep;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "complaintId", nullable = false)
    private Complaint complaint;

    private LocalDateTime dateRep;

    // Constructeurs
    public ResponseComplaint() {}

    // Getters et Setters
    public int getResponseId() { return responseId; }
    public void setResponseId(int responseId) { this.responseId = responseId; }

    public User getUser() { return user; }  // Nouveau getter
    public void setUser(User user) { this.user = user; }  // Nouveau setter

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public boolean isReadRep() { return isReadRep; }
    public void setReadRep(boolean readRep) { isReadRep = readRep; }

    public Complaint getComplaint() { return complaint; }
    public void setComplaint(Complaint complaint) { this.complaint = complaint; }

    public LocalDateTime getDateRep() { return dateRep; }
    public void setDateRep(LocalDateTime dateRep) { this.dateRep = dateRep; }
}