package com.project.ChatApplication.Controller;
import com.project.ChatApplication.DTO.Request.LoginRequest;
import com.project.ChatApplication.DTO.Request.OTP;
import com.project.ChatApplication.DTO.Response.LoginResponse;
import com.project.ChatApplication.DTO.Request.RegisterRequest;
import com.project.ChatApplication.DTO.Response.RegisterResponse;
import com.project.ChatApplication.Service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<RegisterResponse> signUp(@RequestBody RegisterRequest request){
        System.out.println("Inside Signup...");
      return  authService.register(request);

    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request,HttpServletResponse response){
        return authService.login(request,response);

    }

    @PostMapping("/verify-otp")
    public ResponseEntity<RegisterResponse> verify(@RequestBody OTP request){
        return authService.verifyOtp(request.getEmail(),request.getOtp());
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refreshToken(HttpServletRequest request, HttpServletResponse response){
        return authService.getAccessToken(response,request);
    }

    @GetMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam String email){
        return authService.logout(email);
    }
}
