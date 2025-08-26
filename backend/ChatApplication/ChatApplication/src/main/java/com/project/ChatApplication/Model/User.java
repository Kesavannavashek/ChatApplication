package com.project.ChatApplication.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("users")
public class User {

    @Id
    private String id;
    private String name;
    private String email;
    private String profileUrl;
    private String passwordHash;
    private boolean isOnline;
    private Instant lastSeen;
    private String otp;
    private Instant expiryTime;
    private VerificationStatus verificationStatus;
    private String refreshToken;
}
