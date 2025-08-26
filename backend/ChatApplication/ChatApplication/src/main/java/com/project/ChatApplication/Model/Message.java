package com.project.ChatApplication.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("messages")
@Data
@AllArgsConstructor
public class Message {
    @Id
    private String id;
    private String content;
    private String convoId;
    private String senderId;
    private String receiverId;
    private Instant timeStamp;
}
