package com.buzzsocial.controller;

import com.buzzsocial.model.BuzzReport;
import com.buzzsocial.model.User;
import com.buzzsocial.repository.BuzzReportRepository;
import com.buzzsocial.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Administration API")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BuzzReportRepository buzzReportRepository;

    // In a real app, you would add admin role verification here
    
    @PatchMapping("/users/{id}/ban")
    @Operation(summary = "Ban user", description = "Bans a user by ID")
    public ResponseEntity<Void> banUser(@PathVariable String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setIsBanned(true);
        userRepository.save(user);
        
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/users/{id}/unban")
    @Operation(summary = "Unban user", description = "Unbans a user by ID")
    public ResponseEntity<Void> unbanUser(@PathVariable String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setIsBanned(false);
        userRepository.save(user);
        
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reports")
    @Operation(summary = "Get reports", description = "Returns all buzz reports")
    public ResponseEntity<List<BuzzReport>> getAllReports() {
        return ResponseEntity.ok(buzzReportRepository.findAll());
    }

    @PatchMapping("/reports/{id}/status")
    @Operation(summary = "Update report status", description = "Updates the status of a buzz report")
    public ResponseEntity<Void> updateReportStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> statusUpdate) {
        
        BuzzReport report = buzzReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        
        String status = statusUpdate.get("status");
        if (status != null) {
            report.setStatus(BuzzReport.ReportStatus.valueOf(status.toUpperCase()));
            buzzReportRepository.save(report);
        }
        
        return ResponseEntity.ok().build();
    }
}
