package tn.esprit.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.entity.Complaint;
import tn.esprit.entity.ResponseComplaint;
import tn.esprit.service.ResponseComplaintService;

import java.util.List;

@CrossOrigin("*")
@RestController
@RequestMapping("api/responses")
@Tag(name = "Response to Complaints", description = "Management of responses to complaints")
public class ResponseComplaintController {
    @Autowired
    private ResponseComplaintService responseService;

    @Operation(summary = "Add a response to a complaint", description = "Allows adding a response to an existing complaint.")
    @PostMapping(value="/{complaintId}/{user_id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseComplaint addResponse(@PathVariable int complaintId, @RequestBody ResponseComplaint response,@PathVariable Long user_id) throws Exception {
        System.out.println(response);
        return responseService.addResponse(complaintId, response ,user_id);
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

    @Operation(summary = "Get response by ID", description = "Retrieves a specific response by its ID")
    @GetMapping("/{responseId}")
    public ResponseComplaint getResponseById(@PathVariable int responseId) {
        return responseService.getResponseId(responseId);
    }

    @Operation(summary = "Get unread responses", description = "Retrieves all unread responses ordered by date (descending)")
    @GetMapping("/unread")
    public List<ResponseComplaint> getUnreadResponses() {
        return responseService.getUnreadResponseComplaintsOrderedByDateDesc();
    }

    @Operation(summary = "Mark response as read", description = "Marks a specific response as read by its ID")
    @PutMapping("/mark-as-read/{responseId}")
    public ResponseComplaint markResponseAsRead(@PathVariable int responseId) {
        return responseService.markResponseAsRead(responseId);
    }

    @Operation(summary = "Get complaint associated with a response",
            description = "Retrieves the complaint associated with a specific response by its ID")
    @GetMapping("/{responseId}/complaint")
    public ResponseEntity<Complaint> getComplaintByResponseId(@PathVariable int responseId) {
        Complaint complaint = responseService.getComplaintByResponseId(responseId);
        if (complaint != null) {
            return ResponseEntity.ok(complaint);
        }
        return ResponseEntity.notFound().build();
    }
}