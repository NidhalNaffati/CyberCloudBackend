package tn.esprit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import tn.esprit.entity.Complaint;
import tn.esprit.entity.ResponseComplaint;
import tn.esprit.entity.Role;
import tn.esprit.entity.User;
import tn.esprit.exception.ResourceNotFoundException;
import tn.esprit.repository.IComplaintRepository;
import tn.esprit.repository.IResponseComplaintRepository;
import tn.esprit.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Service
public class ResponseComplaintService {
    @Autowired
    private IResponseComplaintRepository responseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IComplaintRepository complaintRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private DirectEmailService directEmailService;
    @Autowired
    private TemplateEngine templateEngine;

    @Value("${server.port}")
    private String serverPort;

    // ✅ Ajouter une réponse à un complaint spécifique
    public ResponseComplaint addResponse(int complaintId, ResponseComplaint response, Long user_id) throws Exception {
        // Trouver l'utilisateur existant
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new Exception("Utilisateur non trouvé avec l'ID: " + user_id));
        Optional<Complaint> complaintOpt = complaintRepository.findById(complaintId);
        if (complaintOpt.isPresent()) {
            Complaint complaint = complaintOpt.get();
            response.setUser(user);
            response.setComplaint(complaint);
            response.setDateRep(LocalDateTime.now());
            response.setReadRep(false); // Par défaut, la réponse n'est pas lue
            ResponseComplaint savedResponse = responseRepository.save(response);

            // Vérifier si l'utilisateur qui répond est un admin et envoyer un email au créateur de la plainte
            if (user.getRole() == Role.ROLE_ADMIN) {
                User complaintCreator = complaint.getUser();
                if (complaintCreator != null && complaintCreator.getEmail() != null) {
                    try {
                        sendComplaintResponseNotification(complaintCreator, complaint, savedResponse);
                        System.out.println("Email envoyé à " + complaintCreator.getEmail() + " pour la plainte #" + complaint.getComplaintId());
                    } catch (Exception e) {
                        // Log l'erreur mais ne pas bloquer le processus
                        System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
                        e.printStackTrace(); // Afficher la stack trace pour le débogage
                    }
                } else {
                    System.err.println("Impossible d'envoyer l'email: utilisateur ou email null");
                }
            } else {
                System.out.println("L'utilisateur n'est pas un admin, pas d'envoi d'email");
            }

            return savedResponse;
        }
        return null; // Retourne null si la plainte n'existe pas
    }

    // ✅ Modifier une réponse existante
    public ResponseComplaint updateResponse(int responseId, ResponseComplaint updatedResponse) {
        return responseRepository.findById(responseId)
                .map(existingResponse -> {
                    // Conserve l'utilisateur original
                    User originalUser = existingResponse.getUser();

                    // Met à jour uniquement les champs modifiables
                    existingResponse.setContent(updatedResponse.getContent());
                    existingResponse.setDateRep(LocalDateTime.now());

                    // Réassocie l'utilisateur original (évite de le modifier)
                    existingResponse.setUser(originalUser);

                    return responseRepository.save(existingResponse);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Response not found with id: " + responseId));
    }
    // ✅ Supprimer une réponse par ID
    public void deleteResponse(int responseId) {
        responseRepository.deleteById(responseId);
    }

    // ✅ Récupérer toutes les réponses d'un complaint
    public List<ResponseComplaint> getResponsesByComplaint(int complaintId) {
        return responseRepository.findByComplaint_ComplaintId(complaintId);
    }
    public ResponseComplaint getResponseId(int responseId) {
        return responseRepository.findById(responseId).orElse(null);
    }
    // ✅ Récupérer les réponses non lues triées par date
    public List<ResponseComplaint> getUnreadResponseComplaintsOrderedByDateDesc() {
        return responseRepository.findByIsReadRepFalseOrderByDateRepDesc(); // Updated method name
    }

    // ✅ Marquer une réponse comme lue
    public ResponseComplaint markResponseAsRead(int responseId) {
        Optional<ResponseComplaint> responseOpt = responseRepository.findById(responseId);
        System.out.println("Response ID: " + responseOpt.get().getComplaint().getComplaintId());
           List <ResponseComplaint> getAllRespone=  responseRepository.findAll();

         for (ResponseComplaint response : getAllRespone) {

             if (response.getComplaint().getComplaintId() == responseOpt.get().getComplaint().getComplaintId()) {

                 response.setReadRep(true);
                  responseRepository.save(response);
             }
         }
        return null; 
        
    }
    public Complaint getComplaintByResponseId(int responseId) {
        Optional<Complaint> complaintOpt = responseRepository.findComplaintByResponseId(responseId);
        return complaintOpt.orElse(null);
    }

    /**
     * Envoie une notification par email à l'utilisateur qui a créé la plainte
     * lorsqu'un admin ajoute une réponse
     *
     * @param user L'utilisateur qui a créé la plainte
     * @param complaint La plainte concernée
     * @param response La réponse ajoutée par l'admin
     */
    private void sendComplaintResponseNotification(User user, Complaint complaint, ResponseComplaint response) {
        try {
            System.out.println("Début de la préparation de l'email pour " + user.getEmail());

            // Préparer le sujet de l'email
            String subject = "Nouvelle réponse à votre plainte #" + complaint.getComplaintId();

            // Préparer le contexte Thymeleaf
            Context context = new Context();
            context.setVariable("firstName", user.getFirstName());
            context.setVariable("subject", complaint.getSubject());
            context.setVariable("responseContent", response.getContent());
            context.setVariable("complaintId", complaint.getComplaintId());
            context.setVariable("loginUrl", "http://localhost:4200/auth/login");
            context.setVariable("currentYear", String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

            // Générer le contenu HTML avec Thymeleaf
            String htmlContent = templateEngine.process("complaint-response-notification", context);

            System.out.println("Contenu HTML généré avec succès, envoi de l'email à " + user.getEmail());

            // Essayer d'abord avec DirectEmailService
            boolean sent = directEmailService.sendEmail(user.getEmail(), subject, htmlContent);

            if (!sent) {
                // Si ça échoue, essayer avec EmailService comme fallback
                System.out.println("Tentative avec EmailService comme fallback...");
                emailService.sendHtmlEmail(user.getEmail(), subject, htmlContent);
            }

            System.out.println("Email envoyé avec succès à " + user.getEmail());
        } catch (Exception e) {
            System.err.println("Erreur dans sendComplaintResponseNotification: " + e.getMessage());
            e.printStackTrace();
        }
    }
}