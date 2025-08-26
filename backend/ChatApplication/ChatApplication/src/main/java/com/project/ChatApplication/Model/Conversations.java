package com.project.ChatApplication.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("conversations")
public class Conversations {

    @Id
    private String id;

    private List<String> participants;

    private LastMessage lastMessage;

    private ConvoType type;

    private Instant lastUpdated;

    private Map<String,Integer> unreadCount;

    private String groupName;

    private String groupAvatar;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class LastMessage{
         private String senderId;
         private String content;
         private Instant timeStamp;
     }

     public enum ConvoType{
        GROUP,PRIVATE
     }
}
