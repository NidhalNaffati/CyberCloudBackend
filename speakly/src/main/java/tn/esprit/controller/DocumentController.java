package tn.esprit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.dto.DocumentDTO;
import tn.esprit.entity.MedecinDocument;
import tn.esprit.entity.User;
import tn.esprit.exception.DocumentNotFoundException;
import tn.esprit.repository.MedecinDocumentRepository;
import tn.esprit.requests.DocumentVerificationRequest;
import tn.esprit.service.MedecinDocumentService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final MedecinDocumentService medecinDocumentService;
    private final MedecinDocumentRepository medecinDocumentRepository;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadMedecinDocuments(
        @RequestParam("email") String email,
        @RequestParam("file") MultipartFile file) {

        medecinDocumentService.uploadMedecinDocuments(email, file);
        return ResponseEntity.ok("Documents uploaded successfully. They will be reviewed by an admin.");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/verify")
    public ResponseEntity<String> verifyMedecinDocuments(@RequestBody DocumentVerificationRequest request) {
        medecinDocumentService.verifyMedecinDocuments(request.email(), request.isVerified());
        if (request.isVerified()) {
            return ResponseEntity.ok("Documents verified and account activated successfully");
        } else {
            return ResponseEntity.ok("Documents rejected");
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/pending")
    public ResponseEntity<List<User>> getPendingVerifications() {
        return ResponseEntity.ok(medecinDocumentService.getMedecinsWithPendingVerification());
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MEDECIN')")
    @GetMapping("/user/{email}")
    public ResponseEntity<List<DocumentDTO>> getUserDocuments(@PathVariable String email) {
        List<MedecinDocument> documents = medecinDocumentService.getMedecinDocuments(email);

        List<DocumentDTO> documentDTOs = documents.stream()
            .map(doc -> new DocumentDTO(
                doc.getId(),
                doc.getDocumentName(),
                doc.getDocumentType(),
                doc.getDocumentSize(),
                doc.getUploadDate()
            ))
            .collect(Collectors.toList());

        return ResponseEntity.ok(documentDTOs);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_MEDECIN')")
    @GetMapping("/download/{documentId}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable Long documentId) {
        MedecinDocument document = medecinDocumentRepository.findById(documentId)
            .orElseThrow(() -> new DocumentNotFoundException());

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(document.getDocumentType()))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getDocumentName() + "\"")
            .body(new ByteArrayResource(document.getDocumentData()));
    }
}