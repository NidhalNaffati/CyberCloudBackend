package tn.esprit.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.service.ActivityImageService;

import java.io.IOException;

@RestController
@RequestMapping("/activity-images")
public class ActivityImageController {

    private final ActivityImageService activityImageService;
    private static final Logger logger = LoggerFactory.getLogger(ActivityImageController.class);
    @Autowired
    public ActivityImageController(ActivityImageService activityImageService) {
        this.activityImageService = activityImageService;
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload an activity image")
    public ResponseEntity<String> uploadActivityImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = activityImageService.uploadActivityImage(file);
            return new ResponseEntity<>(imageUrl, HttpStatus.CREATED);
        } catch (IOException e) {
            logger.error("File upload failed due to an I/O error: {}", e.getMessage());
            return new ResponseEntity<>("File upload failed due to an I/O error.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Unexpected error during file upload: {}", e.getMessage());
            return new ResponseEntity<>("An unexpected error occurred during file upload.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteActivityImage(@RequestParam("publicId") String publicId) {
        try {
            activityImageService.deleteActivityImage(publicId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IOException e) {
            logger.error("Failed to delete image due to an I/O error: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Unexpected error during image deletion: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}