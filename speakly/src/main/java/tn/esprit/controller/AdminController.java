package tn.esprit.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.dto.MedecinDocumentDto;
import tn.esprit.dto.MedecinDto;
import tn.esprit.service.MedecinDocumentService;
import tn.esprit.service.UserService;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@AllArgsConstructor
public class AdminController {

    private final UserService userService;
    private final MedecinDocumentService medecinDocumentService;

    @GetMapping("/medecins")
    public ResponseEntity<List<MedecinDto>> getAllMedecins() {
        log.info("Getting all medecins");
        List<MedecinDto> medecins = userService.getAllMedecins();
        return ResponseEntity.ok(medecins);
    }

    @PostMapping("/verify-medecin/{email}")
    public ResponseEntity<String> verifyMedecinDocuments(
        @PathVariable String email,
        @RequestBody(required = false) Map<String, Boolean> requestBody) {

        // Default to verifying if no specific request is made
        boolean verified = true;

        // If the request contains a verified flag, use it
        if (requestBody != null && requestBody.containsKey("verified")) {
            verified = requestBody.get("verified");
        }

        log.info("Setting document verification status to {} for medecin with email: {}", verified, email);

        if (verified) {
            userService.verifyMedecinDocuments(email);
        } else {
            userService.unverifyMedecinDocuments(email);
        }

        return ResponseEntity.ok("Document verification status updated successfully");
    }

    @PostMapping("/lock-user/{email}")
    public ResponseEntity<String> lockUser(@PathVariable String email) {
        log.info("Locking user account with email: {}", email);
        userService.lockUser(email);
        return ResponseEntity.ok("User locked successfully");
    }

    @PostMapping("/unlock-user/{email}")
    public ResponseEntity<String> unlockUser(@PathVariable String email) {
        log.info("Unlocking user account with email: {}", email);
        userService.unlockUser(email);
        return ResponseEntity.ok("User unlocked successfully");
    }

    @GetMapping("/medecin-documents/{medecinId}")
    public ResponseEntity<MedecinDocumentDto> getMedecinDocuments(@PathVariable Long medecinId) {
        log.info("Getting documents for medecin with ID: {}", medecinId);
        MedecinDocumentDto document = medecinDocumentService.getMedecinDocumentByUserId(medecinId);

        if (document == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(document);
    }
}