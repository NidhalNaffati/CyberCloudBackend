package tn.esprit.service;

import tn.esprit.DTO.FactureDTO;
import tn.esprit.entity.Facture;

import java.util.List;

public interface FactureService {

    Facture ajouterFacture(Facture facture, Long idDoctor, Long idPatient);

    List<Facture> getAllFactures();

    Facture getFactureById(Long id);

    Facture modifierFacture(Long id, FactureDTO facture);
    Facture updateStatus(Long id,String status);

    void supprimerFacture(Long id);

    List<Facture> getFactureByPatientId(Long patientId);
}
