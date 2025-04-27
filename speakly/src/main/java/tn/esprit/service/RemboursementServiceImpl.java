package tn.esprit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.entity.Remboursement;
import tn.esprit.entity.User;
import tn.esprit.repository.RemboursementRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RemboursementServiceImpl implements RemboursementService{
    private final RemboursementRepository remboursementRepository;

    private final FactureService factureService;
    private final EmailService mailingService;

    @Override
    public Remboursement ajouterRemboursement(Remboursement remboursement, Long idFacture) {
        remboursement.setFacture(this.factureService.getFactureById(idFacture));
        remboursement.setStatut("processing");
        remboursement.setDateRemboursement(LocalDate.now());
        return remboursementRepository.save(remboursement);
    }

    @Override
    public List<Remboursement> getAllRemboursements() {
        return remboursementRepository.findAll();
    }

    @Override
    public Remboursement getRemboursementByFactureId(Long factureId) {
        return remboursementRepository.findByFactureId(factureId).orElse(null);

    }

    @Override
    public Remboursement getRemboursementById(Long id) {
        return remboursementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Remboursement non trouvé avec l'ID : " + id));
    }

    @Override
    public Remboursement modifierRemboursement(Long id, Remboursement remboursement) {
        Remboursement existant = getRemboursementById(id);
        existant.setDateRemboursement(remboursement.getDateRemboursement());
         existant.setStatut(remboursement.getStatut());
        existant.setRaison(remboursement.getRaison());
        return remboursementRepository.save(existant);
    }

    @Override
    public void supprimerRemboursement(Long id) {
        if (!remboursementRepository.existsById(id)) {
            throw new RuntimeException("Remboursement introuvable avec l'ID : " + id);
        }
        remboursementRepository.deleteById(id);
    }

    @Override
    public Remboursement accepteRemboursement(Long id) {
        Remboursement remboursement = getRemboursementById(id);
        remboursement.setStatut("accepted");
        remboursement.setDateResponse(LocalDate.now());
        Remboursement saved= this.remboursementRepository.save(remboursement);
            // get user from facture

        try {
            User patient =remboursement.getFacture().getPatient();
            String email=patient.getEmail();
            String nom=patient.getFirstName() + "  "+ patient.getLastName();
            mailingService.sendRemboursementDecisionEmail(email,nom,saved);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return saved;
    }

    @Override
    public Remboursement declineRemboursement(Long id,String raison) {
        Remboursement remboursement = getRemboursementById(id);
        remboursement.setStatut("declined");
        remboursement.setRaison(raison);
        remboursement.setDateResponse(LocalDate.now());
        Remboursement saved= this.remboursementRepository.save(remboursement);

        try {
            User patient =remboursement.getFacture().getPatient();
            String email=patient.getEmail();
            String nom=patient.getFirstName() + "  "+ patient.getLastName();
            mailingService.sendRemboursementDecisionEmail(email,nom,saved);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return  saved;

    }

}
