package tn.esprit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.DTO.FactureDTO;
import tn.esprit.entity.Facture;
import tn.esprit.entity.Remboursement;
import tn.esprit.entity.Role;
import tn.esprit.entity.User;
import tn.esprit.repository.RemboursementRepository;
import tn.esprit.service.FactureService;
import tn.esprit.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/facture")
@RequiredArgsConstructor
public class FactureController {

    private final FactureService factureService;
    private final RemboursementRepository rm;
    private final UserService userService;

    @PostMapping("/{doctorId}/{patientId}")
    public ResponseEntity<Facture> ajouterFacture(@RequestBody Facture facture, @PathVariable Long doctorId, @PathVariable Long patientId) {
        Facture nouvelleFacture = factureService.ajouterFacture(facture, doctorId, patientId);
        return ResponseEntity.ok(nouvelleFacture);
    }
    @GetMapping("/getUserForFacture/{role}")
    public ResponseEntity<List<User>>getUserForFacture(@PathVariable Role role){
        List<User> list=userService.getAllUsersByRole(role);
        return ResponseEntity.ok(list);
    }

    @GetMapping
    public ResponseEntity<List<Facture>> getAllFactures() {
        return ResponseEntity.ok(factureService.getAllFactures());
    }

    @GetMapping ("/myfacture/{patientId}")
    public  ResponseEntity<List<Facture>>getMyFactures(@PathVariable Long patientId){
        return  ResponseEntity.ok(factureService.getFactureByPatientId(patientId));
    }
    @GetMapping("/{id}")
    public ResponseEntity<Facture> getFactureById(@PathVariable Long id) {
        return ResponseEntity.ok(factureService.getFactureById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Facture> modifierFacture(@PathVariable Long id, @RequestBody FactureDTO facture) {
        Facture updatedFacture = factureService.modifierFacture(id, facture);
        return ResponseEntity.ok(updatedFacture);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerFacture(@PathVariable Long id) {
        Optional<Remboursement> r=rm.findByFactureId(id);
        if(r.isPresent()){
            rm.delete(r.get());
        }

        factureService.supprimerFacture(id);
        return ResponseEntity.noContent().build();
    }


}
