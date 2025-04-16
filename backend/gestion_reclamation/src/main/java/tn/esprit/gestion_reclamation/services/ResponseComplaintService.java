package tn.esprit.gestion_reclamation.services;

import tn.esprit.gestion_reclamation.entities.Complaint;
import tn.esprit.gestion_reclamation.entities.ResponseComplaint;
import tn.esprit.gestion_reclamation.repositories.IComplaintRepository;
import tn.esprit.gestion_reclamation.repositories.IResponseComplaintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ResponseComplaintService {
    @Autowired
    private IResponseComplaintRepository responseRepository;

    @Autowired
    private IComplaintRepository complaintRepository;

    // ✅ Ajouter une réponse à un complaint spécifique
    public ResponseComplaint addResponse(int complaintId, ResponseComplaint response) {
        Optional<Complaint> complaintOpt = complaintRepository.findById(complaintId);
        if (complaintOpt.isPresent()) {
            response.setComplaint(complaintOpt.get());
            response.setDateRep(LocalDateTime.now());
            return responseRepository.save(response);
        }
        return null; // Retourne null si la plainte n'existe pas
    }

    // ✅ Modifier une réponse existante
    public ResponseComplaint updateResponse(int responseId, ResponseComplaint updatedResponse) {
        Optional<ResponseComplaint> existingResponseOpt = responseRepository.findById(responseId);
        if (existingResponseOpt.isPresent()) {
            ResponseComplaint existingResponse = existingResponseOpt.get();
            existingResponse.setContent(updatedResponse.getContent());
            existingResponse.setUserId(updatedResponse.getUserId());
            existingResponse.setDateRep(LocalDateTime.now());
            return responseRepository.save(existingResponse);
        }
        return null; // Retourne null si la réponse n'existe pas
    }

    // ✅ Supprimer une réponse par ID
    public void deleteResponse(int responseId) {
        responseRepository.deleteById(responseId);
    }

    // ✅ Récupérer toutes les réponses d’un complaint
    public List<ResponseComplaint> getResponsesByComplaint(int complaintId) {
        return responseRepository.findByComplaint_ComplaintId(complaintId);
    }
}
