package tn.esprit.DTO;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
 @Data
@NoArgsConstructor
@AllArgsConstructor
public class FactureDTO {

    Long id;

    Double montant;
    LocalDate date;
    String statut;
    String reference;


    Long doctorId;

    Long patientId;
}
