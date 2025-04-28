package tn.esprit.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Random;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Facture implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Double montant;
    LocalDate date;
    String statut;
    String reference;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = true)
    User doctor;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    User patient;

    // Method to generate a random 10-character alphanumeric reference
    @PrePersist
    public void generateReference() {
        if (this.reference == null) {
            this.reference = generateRandomReference(10);
        }
    }

    private static String generateRandomReference(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }
}
