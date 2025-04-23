package tn.esprit.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "medecin_document")
public class MedecinDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "document_data", columnDefinition = "LONGBLOB")
    private byte[] documentData;

    @Column(name = "document_name")
    private String documentName;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "document_size")
    private Long documentSize;

    @OneToOne  // Changed from @ManyToOne to @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "upload_date")
    private Date uploadDate;

    @PrePersist
    protected void onCreate() {
        uploadDate = new Date();
    }
}