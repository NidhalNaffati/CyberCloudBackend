package com.example.gestion_forum.controllers;

import com.example.gestion_forum.models.User;
import com.example.gestion_forum.services.AuthService;
import com.example.gestion_forum.dtos.UserRegistrationDTO;  // ✅ Ensure this import is present
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody UserRegistrationDTO userData) {
        User user = authService.registerUser(userData.getUsername(), userData.getEmail(), userData.getPassword());
        return ResponseEntity.ok(user);
    }
}
