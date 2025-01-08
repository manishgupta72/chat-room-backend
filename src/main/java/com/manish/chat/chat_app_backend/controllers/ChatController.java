package com.manish.chat.chat_app_backend.controllers;


import com.manish.chat.chat_app_backend.entities.Message;
import com.manish.chat.chat_app_backend.entities.Room;
import com.manish.chat.chat_app_backend.payload.MessageRequest;
import com.manish.chat.chat_app_backend.repositories.RoomRepository;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

@Controller
public class ChatController {

    private RoomRepository roomRepository;

    public ChatController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }


    //for sending and receiving messages

    @MessageMapping("/sendMessage/{roomId}")  // /app/sendMessage/roomId , for send message
    @SendTo("/topic/room/{roomId}")   //subscribe the client
    public Message sendMessage(@DestinationVariable String roomId,@RequestBody MessageRequest request)
    {
       Room room = roomRepository.findByRoomId(request.getRoomId());
       Message message = new Message();
       message.setContent(request.getContent());
       message.setSender(request.getSender());
       message.setTimeStamp(LocalDateTime.now());
       if(room != null)
       {
            room.getMessages().add(message);
            roomRepository.save(room);
       }else{
           throw new RuntimeException("Error : Room not found");
       }
       return message;
    }
}
