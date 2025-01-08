package com.manish.chat.chat_app_backend.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/profile")
@CrossOrigin("http://localhost:5173")
public class ProfileController {

    @Value("${profile.image.directory}")
    private String imageDirectory;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadProfileImage(@RequestParam("file") MultipartFile file) {
        try {
            // Generate a unique filename
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            // Save the file
            File imageFile = new File(imageDirectory + File.separator + fileName);
            file.transferTo(imageFile);

            // Return file URL
            String fileUrl = "/api/v1/profile/images/" + fileName;
            return ResponseEntity.ok(Map.of("message", "Image uploaded successfully", "url", fileUrl));
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Failed to upload image");
        }
    }

    @GetMapping("/images/{filename}")
    public ResponseEntity<?> getProfileImage(@PathVariable String filename) {
        File imageFile = new File(imageDirectory + File.separator + filename);
        if (!imageFile.exists()) {
            return ResponseEntity.status(404).body("Image not found");
        }

        return ResponseEntity.ok()
                .header("Content-Type", "image/jpeg")
                .body(imageFile);
    }
}
