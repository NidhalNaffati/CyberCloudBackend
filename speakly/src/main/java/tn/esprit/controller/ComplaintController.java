package tn.esprit.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import tn.esprit.entity.Complaint;
import tn.esprit.service.BadWordsService;
import tn.esprit.service.ComplaintService;

import java.util.List;

@CrossOrigin("*")

@RestController
@RequestMapping("api/complaints")
@Tag(name = "Complaints", description = "Management of user complaints") // ✅ Added the Swagger tag in English
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;
    @Autowired
    private BadWordsService badWordsService;
    private void validateComplaintContent(Complaint complaint) {
        String contentCheck = badWordsService.checkForBadWords(complaint.getContent());
        String subjectCheck = badWordsService.checkForBadWords(complaint.getSubject());

        if ("YES".equalsIgnoreCase(contentCheck)) {
            throw new IllegalArgumentException("Le contenu de la plainte contient des mots inappropriés");
        }

        if ("YES".equalsIgnoreCase(subjectCheck)) {
            throw new IllegalArgumentException("Le sujet de la plainte contient des mots inappropriés");
        }
    }
    @Operation(summary = "Add a complaint", description = "Allows a user to add a new complaint.")
    @PostMapping(value = "/{user_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Complaint addComplaint(@RequestBody Complaint complaint, @PathVariable Long user_id) throws Exception {
        System.out.println(complaint);
        validateComplaintContent(complaint); // Validation avant traitement
        return complaintService.addComplaint(complaint, user_id);
    }

    @Operation(summary = "Update a complaint", description = "Allows updating an existing complaint by its ID.")
    @PutMapping("/{complaintId}")
    public Complaint updateComplaint(@PathVariable int complaintId, @RequestBody Complaint complaint) {
        validateComplaintContent(complaint); // Validation avant traitement
        return complaintService.updateComplaintById(complaintId, complaint);
    }


    @Operation(summary = "Delete a complaint", description = "Allows deleting an existing complaint by its ID.")
    @DeleteMapping("/{complaintId}")
    public void deleteComplaint(@PathVariable int complaintId) {
        complaintService.deleteComplaint(complaintId);
    }

    @Operation(summary = "List all complaints", description = "Allows retrieving all existing complaints.")
    @GetMapping
    public List<Complaint> getAllComplaints() {
        return complaintService.getAllComplaints();
    }

    @Operation(summary = "Get details of a complaint", description = "Allows retrieving details of a specific complaint by its ID.")
    @GetMapping("/{complaintId}")
    public Complaint getComplaintById(@PathVariable int complaintId) {
        return complaintService.getComplaintById(complaintId);
    }
    @Operation(summary = "Get complaints by user ID", description = "Allows retrieving all complaints submitted by a specific user.")
    @GetMapping("/user/{userId}")
    public List<Complaint> getComplaintsByUser(@PathVariable int userId) {
        return complaintService.getComplaintsByUser(userId);
    }
    @GetMapping("/unread")
    public List<Complaint> getUnreadComplaintsOrderedByDate() {
        return complaintService.getUnreadComplaintsOrderedByDateDesc();
    }

    @Operation(summary = "Mark a complaint as read", description = "Allows marking a complaint as read.")
    @PutMapping("/{complaintId}/read")
    public Complaint markComplaintAsRead(@PathVariable int complaintId) {
        return complaintService.markComplaintAsRead(complaintId);
    }

}