package com.project.ChatApplication.JWTUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.function.Function;

@Component
public class JWT {

    private final SecretKey jwtSecret;

    public JWT(@Value("${jwt.secret}") String secret){
        this.jwtSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }

    public String generateJwt(String email) {
        Date expiryDate = Date.from(Instant.now().plus(Duration.ofDays(1)));
        String token = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, jwtSecret)
                .compact();

        return Jwts.builder().setSubject(email).setIssuedAt(new Date()).setExpiration(expiryDate).signWith(SignatureAlgorithm.HS256, jwtSecret).compact();
    }

    public String generateRefreshToken(String email){
        return Jwts.builder().setSubject(email).setIssuedAt(new Date()).signWith(SignatureAlgorithm.HS256,jwtSecret).compact();
    }

    public String extractEmail(String token){
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractEmail(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    public boolean isRefreshTokenValid(String token, String username) {
        final String extractedUsername = extractEmail(token);
        return extractedUsername.equals(username);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }


}
