package tn.esprit.service;


import tn.esprit.entity.Remboursement;

import java.util.List;

public interface RemboursementService {

    Remboursement ajouterRemboursement(Remboursement remboursement, Long idFacture);

    List<Remboursement> getAllRemboursements();

    Remboursement getRemboursementByFactureId(Long factureId);

    Remboursement getRemboursementById(Long id);

    Remboursement modifierRemboursement(Long id, Remboursement remboursement);

    void supprimerRemboursement(Long id);

    Remboursement accepteRemboursement(Long id);

    Remboursement declineRemboursement(Long id,String raison);
}
