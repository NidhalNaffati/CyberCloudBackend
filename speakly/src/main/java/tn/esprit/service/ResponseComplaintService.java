package tn.esprit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.entity.Complaint;
import tn.esprit.entity.ResponseComplaint;
import tn.esprit.entity.User;
import tn.esprit.exception.ResourceNotFoundException;
import tn.esprit.repository.IComplaintRepository;
import tn.esprit.repository.IResponseComplaintRepository;
import tn.esprit.repository.UserRepository;

import java.time.LocalDateTime;
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

    // ✅ Ajouter une réponse à un complaint spécifique
    public ResponseComplaint addResponse(int complaintId, ResponseComplaint response, Long user_id) throws Exception {
        // Trouver l'utilisateur existant
        User user = userRepository.findById(user_id)
                .orElseThrow(() -> new Exception("Utilisateur non trouvé avec l'ID: " + user_id));
        Optional<Complaint> complaintOpt = complaintRepository.findById(complaintId);
        if (complaintOpt.isPresent()) {
            response.setUser(user);
            response.setComplaint(complaintOpt.get());
            response.setDateRep(LocalDateTime.now());
            response.setReadRep(false); // Par défaut, la réponse n'est pas lue
            return responseRepository.save(response);
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
        if (responseOpt.isPresent()) {
            ResponseComplaint response = responseOpt.get();
            response.setReadRep(true);
            return responseRepository.save(response);
        }
        return null; // Retourne null si la réponse n'existe pas
    }
    public Complaint getComplaintByResponseId(int responseId) {
        Optional<Complaint> complaintOpt = responseRepository.findComplaintByResponseId(responseId);
        return complaintOpt.orElse(null);
    }
}