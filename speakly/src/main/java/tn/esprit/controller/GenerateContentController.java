package tn.esprit.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.service.GenerateContentService;

import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("api/generate-content")
@Tag(name = "Content Generation", description = "API for generating content based on subject")
public class GenerateContentController {

    @Autowired
    private GenerateContentService generateContentService;

    @Operation(summary = "Generate content", description = "Generates content based on a given subject")
    @PostMapping
    public ResponseEntity<Map<String, String>> generateContent(@RequestBody Map<String, String> request) {
        String subject = request.get("subject");
        
        if (subject == null || subject.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Subject cannot be empty"));
        }
        
        String generatedContent = generateContentService.generateContent(subject);
        return ResponseEntity.ok(Map.of("content", generatedContent));
    }
}
