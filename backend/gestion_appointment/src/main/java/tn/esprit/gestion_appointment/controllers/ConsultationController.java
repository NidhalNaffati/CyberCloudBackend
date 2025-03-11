package tn.esprit.gestion_appointment.controllers;





import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.gestion_appointment.entities.Consultation;
import tn.esprit.gestion_appointment.services.ConsultationService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/consultations")
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    @GetMapping
    public List<Consultation> getAllConsultations() {
        return consultationService.getAllConsultations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Consultation> getConsultationById(@PathVariable Long id) {
        Optional<Consultation> consultation = consultationService.getConsultationById(id);
        return consultation.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Consultation createConsultation(@RequestBody Consultation consultation) {
        return consultationService.createConsultation(consultation);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Consultation> updateConsultation(@PathVariable Long id, @RequestBody Consultation newConsultation) {
        Consultation updatedConsultation = consultationService.updateConsultation(id, newConsultation);
        return updatedConsultation != null ? ResponseEntity.ok(updatedConsultation) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConsultation(@PathVariable Long id) {
        consultationService.deleteConsultation(id);
        return ResponseEntity.noContent().build();
    }
}

