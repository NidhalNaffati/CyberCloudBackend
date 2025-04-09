package com.buzzsocial.controller;

import com.buzzsocial.dto.request.CreateAdminRequest;
import com.buzzsocial.dto.response.UserDto;
import com.buzzsocial.model.Buzz;
import com.buzzsocial.model.BuzzComment;
import com.buzzsocial.model.BuzzReport;
import com.buzzsocial.model.User;
import com.buzzsocial.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@Tag(name = "Admin Dashboard", description = "Admin dashboard API")
public class AdminDashboardController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/register")
    @Operation(summary = "Register admin", description = "Creates a new admin account")
    public ResponseEntity<User> registerAdmin(@Valid @RequestBody CreateAdminRequest request) {
        return ResponseEntity.ok(adminService.createAdmin(request));
    }

    @GetMapping("/stats")
    @Operation(summary = "Get dashboard stats", description = "Returns statistics for the admin dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        return ResponseEntity.ok(adminService.getDashboardStats());
    }

    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Returns a paginated list of all users")
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        return ResponseEntity.ok(adminService.getAllUsers(page, size, sortBy, direction));
    }

    @GetMapping("/users/search")
    @Operation(summary = "Search users", description = "Searches for users by query")
    public ResponseEntity<List<User>> searchUsers(@RequestParam String query) {
        return ResponseEntity.ok(adminService.searchUsers(query));
    }

    @GetMapping("/buzzs")
    @Operation(summary = "Get all buzzs", description = "Returns a paginated list of all buzz posts")
    public ResponseEntity<Page<Buzz>> getAllBuzzs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        return ResponseEntity.ok(adminService.getAllBuzzs(page, size, sortBy, direction));
    }

    @GetMapping("/buzzs/search")
    @Operation(summary = "Search buzzs", description = "Searches for buzz posts by query")
    public ResponseEntity<List<Buzz>> searchBuzzs(@RequestParam String query) {
        return ResponseEntity.ok(adminService.searchBuzzs(query));
    }

    @DeleteMapping("/buzzs/{id}")
    @Operation(summary = "Delete buzz", description = "Deletes a buzz post by ID")
    public ResponseEntity<Void> deleteBuzz(@PathVariable String id) {
        adminService.deleteBuzz(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/comments")
    @Operation(summary = "Get all comments", description = "Returns a paginated list of all comments")
    public ResponseEntity<Page<BuzzComment>> getAllComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        return ResponseEntity.ok(adminService.getAllComments(page, size, sortBy, direction));
    }

    @GetMapping("/comments/search")
    @Operation(summary = "Search comments", description = "Searches for comments by query")
    public ResponseEntity<List<BuzzComment>> searchComments(@RequestParam String query) {
        return ResponseEntity.ok(adminService.searchComments(query));
    }

    @DeleteMapping("/comments/{id}")
    @Operation(summary = "Delete comment", description = "Deletes a comment by ID")
    public ResponseEntity<Void> deleteComment(@PathVariable String id) {
        adminService.deleteComment(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reports")
    @Operation(summary = "Get all reports", description = "Returns a paginated list of all reports")
    public ResponseEntity<Page<BuzzReport>> getAllReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {
        return ResponseEntity.ok(adminService.getAllReports(page, size, sortBy, direction));
    }

    @GetMapping("/reports/search")
    @Operation(summary = "Search reports", description = "Searches for reports by query")
    public ResponseEntity<List<BuzzReport>> searchReports(@RequestParam String query) {
        return ResponseEntity.ok(adminService.searchReports(query));
    }

    @PatchMapping("/reports/{id}/status")
    @Operation(summary = "Update report status", description = "Updates the status of a report")
    public ResponseEntity<Void> updateReportStatus(
            @PathVariable String id,
            @RequestParam BuzzReport.ReportStatus status) {
        adminService.updateReportStatus(id, status);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/users/{id}/ban")
    @Operation(summary = "Ban user", description = "Bans a user by ID")
    public ResponseEntity<Void> banUser(@PathVariable String id) {
        adminService.banUser(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/users/{id}/unban")
    @Operation(summary = "Unban user", description = "Unbans a user by ID")
    public ResponseEntity<Void> unbanUser(@PathVariable String id) {
        adminService.unbanUser(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/{id}/followers")
    @Operation(summary = "Get user followers", description = "Returns a list of followers for a user")
    public ResponseEntity<List<UserDto>> getUserFollowers(@PathVariable String id) {
        return ResponseEntity.ok(adminService.getUserFollowers(id));
    }

    @GetMapping("/users/{id}/followings")
    @Operation(summary = "Get user followings", description = "Returns a list of users that a user follows")
    public ResponseEntity<List<UserDto>> getUserFollowings(@PathVariable String id) {
        return ResponseEntity.ok(adminService.getUserFollowings(id));
    }
}
