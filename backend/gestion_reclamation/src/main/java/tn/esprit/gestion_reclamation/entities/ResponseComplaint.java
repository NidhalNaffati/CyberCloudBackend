package tn.esprit.gestion_reclamation.entities;



import jakarta.persistence.*;

@Entity
public class ResponseComplaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int responseId;

    private int userId;
    private String content;

    @ManyToOne
    @JoinColumn(name = "complaintId", nullable = false)
    private Complaint complaint;

    // Constructeurs
    public ResponseComplaint() {}

    // Getters et Setters
    public int getResponseId() { return responseId; }
    public void setResponseId(int responseId) { this.responseId = responseId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Complaint getComplaint() { return complaint; }
    public void setComplaint(Complaint complaint) { this.complaint = complaint; }
}
