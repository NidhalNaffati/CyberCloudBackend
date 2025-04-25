package tn.esprit.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.dto.MedecinDocumentDto;
import tn.esprit.entity.MedecinDocument;
import tn.esprit.entity.Role;
import tn.esprit.entity.User;
import tn.esprit.exception.InvalidDocumentException;
import tn.esprit.exception.UserNotFoundException;
import tn.esprit.repository.MedecinDocumentRepository;
import tn.esprit.repository.UserRepository;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class MedecinDocumentService {

    private final UserRepository userRepository;
    private final MedecinDocumentRepository medecinDocumentRepository;
    private final EmailService emailService;

    @Transactional
    public void uploadMedecinDocuments(String email, MultipartFile file) {
        log.info("Trying to upload document for user with email: {})", email);

        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("No user found with email: " + email));

        // Verify this is a medecin user
        if (user.getRole() != Role.ROLE_MEDECIN) {
            throw new IllegalStateException("User is not registered as a medecin");
        }

        try {
            // Check if user already has a document and delete it if they do
            Optional<MedecinDocument> existingDocument = medecinDocumentRepository.findByUserId(user.getId());
            existingDocument.ifPresent(medecinDocumentRepository::delete);

            // Create document entity
            MedecinDocument document = MedecinDocument.builder()
                .documentData(file.getBytes())
                .documentName(file.getOriginalFilename())
                .documentType(file.getContentType())
                .documentSize(file.getSize())
                .user(user)
                .build();

            // Save document and user
            medecinDocumentRepository.save(document);
            log.info("Document {} uploaded successfully for user {}", document.getDocumentName(), user.getEmail());
            userRepository.save(user);

            // Notify admin about new document submission
            notifyAdminAboutNewSubmission(user);

        } catch (IOException e) {
            log.error("Failed to process document for user {}: {}", email, e.getMessage());
            throw new InvalidDocumentException();
        }
    }

    public MedecinDocumentDto getMedecinDocumentByUserId(Long userId) {
        Optional<MedecinDocument> documentOpt = medecinDocumentRepository.findByUserId(userId);

        if (documentOpt.isPresent()) {
            MedecinDocument document = documentOpt.get();
            User user = document.getUser();

            return MedecinDocumentDto.builder()
                .id(document.getId())
                .documentName(document.getDocumentName())
                .documentType(document.getDocumentType())
                .documentSize(document.getDocumentSize())
                .documentData(Base64.getEncoder().encodeToString(document.getDocumentData()))
                .uploadDate(document.getUploadDate())
                .userDocumentsVerified(user.getDocumentsVerified())
                .build();
        }

        return null;
    }

    private void notifyAdminAboutNewSubmission(User user) {
        try {
            // Get admin emails
            List<String> adminEmails = userRepository.findEmailsByRole(Role.ROLE_ADMIN);

            if (adminEmails.isEmpty()) {
                log.warn("No admin users found to notify about new document submission");
                return;
            }

            for (String adminEmail : adminEmails) {
                emailService.sendNewMedecinDocumentSubmissionNotification(
                    adminEmail,
                    user.getFirstName() + " " + user.getLastName()
                );
            }
        } catch (Exception e) {
            // Log the error but don't fail the registration process
            log.error("Failed to notify admins about new document submission: {}", e.getMessage());
        }
    }
}