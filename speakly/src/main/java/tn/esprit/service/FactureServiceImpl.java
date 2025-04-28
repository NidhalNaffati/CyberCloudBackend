package tn.esprit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.DTO.FactureDTO;
import tn.esprit.entity.Facture;
import tn.esprit.entity.User;
import tn.esprit.repository.FactureRepository;
import tn.esprit.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FactureServiceImpl implements FactureService {

    private final FactureRepository factureRepository;


    private final UserRepository userRepository;
    @Override
    public Facture ajouterFacture(Facture facture, Long idDoctor, Long idPatient) {
        User doctor = userRepository.findById(idDoctor)
                .orElseThrow(() -> new RuntimeException("USER NOT FOUND!"));
        User patient=userRepository.findById(idPatient)
                .orElseThrow(() -> new RuntimeException("USER NOT FOUND!"));

        facture.setDoctor(doctor);
        facture.setPatient(patient);
        facture.setDate(LocalDate.now());
        facture.setStatut("Not paid");
        return factureRepository.save(facture);
    }

    @Override
    public List<Facture> getAllFactures() {
        return factureRepository.findAll();
    }

    @Override
    public Facture getFactureById(Long id) {
        return factureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée avec l'ID : " + id));
    }

    @Override
    public Facture modifierFacture(Long id, FactureDTO nouvelleFacture) {
        Facture factureExistante = getFactureById(id);
        if(nouvelleFacture.getMontant()!=null && nouvelleFacture.getMontant()!=0)
        factureExistante.setMontant(nouvelleFacture.getMontant());
        if(nouvelleFacture.getDoctorId()!=null&& nouvelleFacture.getDoctorId()!=0)
        {
             User doctor = userRepository.findById(nouvelleFacture.getDoctorId())
                    .orElseThrow(() -> new RuntimeException("USER NOT FOUND!"));

            factureExistante.setDoctor(doctor);
        }
        if(nouvelleFacture.getPatientId()!=null&& nouvelleFacture.getPatientId()!=0) {
            User patient = userRepository.findById(nouvelleFacture.getPatientId())
                    .orElseThrow(() -> new RuntimeException("USER NOT FOUND!"));

             factureExistante.setPatient(patient);
        }
        if(nouvelleFacture.getStatut()!=null){
            factureExistante.setStatut(nouvelleFacture.getStatut());
        }
        return factureRepository.save(factureExistante);
    }

    @Override
    public Facture updateStatus(Long id, String status) {
        Facture f=factureRepository.findById(id).orElseThrow();
        f.setStatut(status);
        return factureRepository.save(f);

    }

    @Override
    public void supprimerFacture(Long id) {
        if (!factureRepository.existsById(id)) {
            throw new RuntimeException("Impossible de supprimer : Facture non trouvée avec l'ID : " + id);
        }
        factureRepository.deleteById(id);
    }

    @Override
    public List<Facture> getFactureByPatientId(Long patientId) {
        User patient = userRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("USER NOT FOUND!"));

        return factureRepository.findFactureByPatient(patient);
     }

}
