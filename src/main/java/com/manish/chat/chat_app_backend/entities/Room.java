package com.manish.chat.chat_app_backend.entities;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    private String id; // MongoDB: unique identifier
    private String roomId;
    private List<Message> messages = new ArrayList<>();
    private List<UserProfile> userProfiles = new ArrayList<>(); // New field for user profiles
}
