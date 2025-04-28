package tn.esprit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.entity.Remboursement;
import tn.esprit.service.RemboursementService;

import java.util.List;

@RestController
@RequestMapping("/remboursement")
@RequiredArgsConstructor
public class RemboursementController {

    private final RemboursementService remboursementService;

    @PostMapping("/{idFacture}")
    public ResponseEntity<Remboursement> ajouterRemboursement(@RequestBody Remboursement remboursement, @PathVariable Long idFacture) {
        Remboursement nouveauRemboursement = remboursementService.ajouterRemboursement(remboursement, idFacture);
        return ResponseEntity.ok(nouveauRemboursement);
    }

    @GetMapping("/byFactureId/{factureId}")
    public ResponseEntity<Remboursement> getByFactureId(@PathVariable Long factureId) {
        return ResponseEntity.ok(remboursementService.getRemboursementByFactureId(factureId));
    }

    @GetMapping
    public ResponseEntity<List<Remboursement>> getAllRemboursements() {
        return ResponseEntity.ok(remboursementService.getAllRemboursements());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Remboursement> getRemboursementById(@PathVariable Long id) {
        return ResponseEntity.ok(remboursementService.getRemboursementById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Remboursement> modifierRemboursement(@PathVariable Long id, @RequestBody Remboursement remboursement) {
        Remboursement updatedRemboursement = remboursementService.modifierRemboursement(id, remboursement);
        return ResponseEntity.ok(updatedRemboursement);
    }

    @PutMapping("/accept/{id}")
    public ResponseEntity<Remboursement> acceptRemboursement(@PathVariable Long id) {
        return ResponseEntity.ok(remboursementService.accepteRemboursement(id));
    }

    @PutMapping(value = "/decline/{id}", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Remboursement> declineRemboursement(@PathVariable Long id,@RequestBody String raison) {
        return ResponseEntity.ok(remboursementService.declineRemboursement(id,raison));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerRemboursement(@PathVariable Long id) {
        remboursementService.supprimerRemboursement(id);
        return ResponseEntity.noContent().build();
    }

}
