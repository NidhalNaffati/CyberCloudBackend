package tn.esprit.gestion_reclamation.Controller;

import tn.esprit.gestion_reclamation.entities.ResponseComplaint;
import tn.esprit.gestion_reclamation.services.ResponseComplaintService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("/responses")
@Tag(name = "Response to Complaints", description = "Management of responses to complaints") // ✅ Updated the tag to English
public class ResponseComplaintController {
    @Autowired
    private ResponseComplaintService responseService;

    @Operation(summary = "Add a response to a complaint", description = "Allows adding a response to an existing complaint.")
    @PostMapping("/{complaintId}")
    public ResponseComplaint addResponse(@PathVariable int complaintId, @RequestBody ResponseComplaint response) {
        return responseService.addResponse(complaintId, response);
    }

    @Operation(summary = "Update a response", description = "Allows updating an existing response by its ID.")
    @PutMapping("/{responseId}")
    public ResponseComplaint updateResponse(@PathVariable int responseId, @RequestBody ResponseComplaint response) {
        return responseService.updateResponse(responseId, response);
    }

    @Operation(summary = "Delete a response", description = "Allows deleting an existing response by its ID.")
    @DeleteMapping("/{responseId}")
    public void deleteResponse(@PathVariable int responseId) {
        responseService.deleteResponse(responseId);
    }

    @Operation(summary = "List all responses to a complaint", description = "Allows retrieving all responses associated with a specific complaint.")
    @GetMapping("/complaint/{complaintId}")
    public List<ResponseComplaint> getResponsesByComplaint(@PathVariable int complaintId) {
        return responseService.getResponsesByComplaint(complaintId);
    }
}
