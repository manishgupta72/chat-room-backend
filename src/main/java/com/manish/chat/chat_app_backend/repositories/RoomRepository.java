package com.manish.chat.chat_app_backend.repositories;

import com.manish.chat.chat_app_backend.entities.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoomRepository extends MongoRepository<Room, String> {

    //get room using room id

    Room findByRoomId(String roomId);
}
