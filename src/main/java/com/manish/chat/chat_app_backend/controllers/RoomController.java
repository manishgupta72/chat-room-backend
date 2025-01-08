package com.manish.chat.chat_app_backend.controllers;

import com.manish.chat.chat_app_backend.entities.Message;
import com.manish.chat.chat_app_backend.entities.Room;
import com.manish.chat.chat_app_backend.entities.UserProfile;
import com.manish.chat.chat_app_backend.repositories.RoomRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rooms")
@CrossOrigin("http://localhost:5173")
public class RoomController {
    // create room

    private RoomRepository roomRepository;

    // Inject the new image directory path from application.properties
    @Value("${profile.image.directory}")
    private String imageDirectory;
    public RoomController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRoomWithProfile(
            @RequestParam("roomId") String roomId,
            @RequestParam("userName") String userName,
            @RequestParam("profileImage") MultipartFile profileImage) {

        // Check if room already exists
        if (roomRepository.findByRoomId(roomId) != null) {
            return ResponseEntity.badRequest().body("Room already exists");
        }

        // Save profile image
        String fileName = UUID.randomUUID() + "_" + profileImage.getOriginalFilename();
        String filePath = imageDirectory + "/" + fileName;

        try {
            File directory = new File(imageDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            File imageFile = new File(filePath);
            profileImage.transferTo(imageFile);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload profile image");
        }

        // Create room and add user profile
        Room room = new Room();
        room.setRoomId(roomId);
        UserProfile userProfile = new UserProfile(userName, "/assets/images/" + fileName);
        room.getUserProfiles().add(userProfile);
        roomRepository.save(room);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Room created successfully",
                "roomId", roomId,
                "userName", userName,
                "profileImageUrl", "/assets/images/" + fileName,
                "userProfiles", room.getUserProfiles()));
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinRoomWithProfile(
            @RequestParam("roomId") String roomId,
            @RequestParam("userName") String userName,
            @RequestParam(value = "profileImage", required = false) MultipartFile profileImage) {

        Room room = roomRepository.findByRoomId(roomId);
        if (room == null) {
            return ResponseEntity.badRequest().body("Room not found");
        }

        // Check if user already exists
        UserProfile existingUser = room.getUserProfiles().stream()
                .filter(profile -> profile.getUserName().equals(userName))
                .findFirst()
                .orElse(null);

        String profileImageUrl = null;

        if (existingUser == null) {
            // Save new profile image if provided
            if (profileImage != null) {
                String fileName = UUID.randomUUID() + "_" + profileImage.getOriginalFilename();
                String filePath = imageDirectory + "/" + fileName;

                try {
                    File directory = new File(imageDirectory);
                    if (!directory.exists()) {
                        directory.mkdirs();
                    }

                    File imageFile = new File(filePath);
                    profileImage.transferTo(imageFile);
                    profileImageUrl = "/assets/images/" + fileName;

                } catch (IOException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Failed to upload profile image");
                }

                // Add new user profile
                UserProfile newUserProfile = new UserProfile(userName, profileImageUrl);
                room.getUserProfiles().add(newUserProfile);
                roomRepository.save(room);
            }
        } else {
            // Use existing profile image
            profileImageUrl = existingUser.getProfileImageUrl();
        }

        return ResponseEntity.ok(Map.of(
                "roomId", roomId,
                "userName", userName,
                "profileImageUrl", profileImageUrl,
                "userProfiles", room.getUserProfiles()));
    }

    // get room:join
    @GetMapping("/{roomId}")
    public ResponseEntity<?> joinRoom(@PathVariable String roomId) {

        Room room = roomRepository.findByRoomId(roomId);
        if (room == null) {
            return ResponseEntity.badRequest().body("Room not found");
        }
        return ResponseEntity.ok(room);
    }

    // get messages of room
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List> getMessages(@PathVariable String roomId,
            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
            @RequestParam(value = "size", defaultValue = "20", required = false) int size) {
        Room room = roomRepository.findByRoomId(roomId);
        if (room == null) {
            return ResponseEntity.badRequest().build();
        }

        // get messages
        // pagination

        List<Message> messages = room.getMessages();

        int start = Math.max(0, messages.size() - (page + 1) * size);
        int end = Math.min(messages.size(), start + size);

        List<Message> paginatedMessages = messages.subList(start, end);

        return ResponseEntity.ok(paginatedMessages);
    }
}
