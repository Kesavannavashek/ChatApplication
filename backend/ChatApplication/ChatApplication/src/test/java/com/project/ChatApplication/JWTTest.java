package com.project.ChatApplication;

import com.project.ChatApplication.JWTUtils.JWT;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JWTTest {

    @Autowired
    private JWT jwt;


    private final SecretKey jwtSecret;

    public JWTTest(@Value("${jwt.secret}") String secret){
        this.jwtSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }


    private final String email = "heyt15306@gmail.com";

    @Test
    void generationTest(){
        String generated = jwt.generateJwt(email);
        String userEmail = jwt.extractEmail(generated);
        assertEquals(email, userEmail, "Email should match from token");
    }

    @Test
    void testTokenIsValid() {
        String token = jwt.generateJwt(email);

        boolean valid = jwt.isTokenValid(token, email);

        assertTrue(valid, "Generated token should be valid for the same user");
    }

    @Test
    void testInvalidToken() {
        String token = jwt.generateJwt(email);

        boolean valid = jwt.isTokenValid(token, "another@example.com");

        assertFalse(valid, "Token should be invalid for another user");
    }

    @Test
    void testTokenExpiry() {
        // Create a short-lived token
        String token = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(Date.from(Instant.now().minus(Duration.ofMinutes(1))))
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();

        assertThrows(ExpiredJwtException.class, () -> {
            jwt.extractEmail(token);
        }, "Expired token should throw ExpiredJwtException");
    }
}
