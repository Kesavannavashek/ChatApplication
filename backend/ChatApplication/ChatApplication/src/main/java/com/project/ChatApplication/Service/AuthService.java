package com.project.ChatApplication.Service;

import com.project.ChatApplication.DTO.Request.LoginRequest;
import com.project.ChatApplication.DTO.Response.LoginResponse;
import com.project.ChatApplication.DTO.Request.RegisterRequest;
import com.project.ChatApplication.DTO.Response.RegisterResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface AuthService {

    ResponseEntity<LoginResponse> login(LoginRequest request, HttpServletResponse response);

    public ResponseEntity<RegisterResponse> register(RegisterRequest request);

    public ResponseEntity<RegisterResponse> verifyOtp(String email, Integer otp);

    ResponseEntity<LoginResponse> getAccessToken(HttpServletResponse response, HttpServletRequest request);

    public ResponseEntity<String> logout(String email);
}
