package tn.esprit.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.entity.Role;
import tn.esprit.entity.User;
import tn.esprit.requests.*;
import tn.esprit.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register/user")
    public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequest request) {
        authenticationService.registerUser(request);
        return ResponseEntity.ok("Registration successful. Check your email for verification code.");
    }

    @PostMapping("/register/medecin")
    public ResponseEntity<String> registerMedecin(
        @RequestPart("request") @Valid RegisterRequest request,
        @RequestPart(value = "document", required = false) MultipartFile document) {

        if (request.role() != Role.ROLE_MEDECIN) {
            return ResponseEntity.badRequest().body("Invalid role for medecin registration");
        }

        authenticationService.registerMedecinUser(request, document);
        return ResponseEntity.ok("Medecin registration successful. Your documents will be verified by an administrator.");
    }

    @PostMapping("/verify-user")
    public ResponseEntity<String> verifyAccount(@RequestBody VerifyAccountRequest request) {
        authenticationService.verifyUserAccount(request.getEmail(), request.getCode());
        return ResponseEntity.ok("Account verified successfully");
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        authenticationService.sendResetPasswordCodeToUser(request.getEmail());
        return ResponseEntity.ok("Password reset code sent to your email");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        authenticationService.resetPassword(
            request.getEmail(),
            request.getCode(),
            request.getNewPassword(),
            request.getConfirmPassword()
        );
        return ResponseEntity.ok("Password reset successful");
    }

    // New endpoint to fetch user profile
    @GetMapping("/profile")
    public ResponseEntity<User> getUserProfile() {
        User user = authenticationService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    // New endpoint to update user profile
    @PutMapping("/profile")
    public ResponseEntity<String> updateUserProfile(@Valid @RequestBody EditProfileRequest request) {
        authenticationService.updateUserProfile(request);
        return ResponseEntity.ok("Profile updated successfully");
    }
}