package tn.esprit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.DTO.FactureDTO;
import tn.esprit.entity.Facture;
import tn.esprit.service.FactureService;

import java.util.List;

@RestController
@RequestMapping("/facture")
@RequiredArgsConstructor
public class FactureController {

    private final FactureService factureService;


    @PostMapping("/{doctorId}/{patientId}")
    public ResponseEntity<Facture> ajouterFacture(@RequestBody Facture facture, @PathVariable Long doctorId, @PathVariable Long patientId) {
        Facture nouvelleFacture = factureService.ajouterFacture(facture, doctorId, patientId);
        return ResponseEntity.ok(nouvelleFacture);
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
        factureService.supprimerFacture(id);
        return ResponseEntity.noContent().build();
    }


}
