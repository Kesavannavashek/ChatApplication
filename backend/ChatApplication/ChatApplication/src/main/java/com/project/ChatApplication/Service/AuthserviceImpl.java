package com.project.ChatApplication.Service;

import com.project.ChatApplication.DTO.Request.LoginRequest;
import com.project.ChatApplication.DTO.Response.LoginResponse;
import com.project.ChatApplication.DTO.Request.RegisterRequest;
import com.project.ChatApplication.DTO.Response.RegisterResponse;
import com.project.ChatApplication.EmailUtils.EmailGeneratorAndSender;
import com.project.ChatApplication.JWTUtils.JWT;
import com.project.ChatApplication.Model.User;
import com.project.ChatApplication.Model.VerificationStatus;
import com.project.ChatApplication.Repository.UsersRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.Cookie;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;

@Service
public class AuthserviceImpl implements AuthService {

    @Autowired
    private EmailGeneratorAndSender otpService;


    @Autowired
    private JWT jwt;

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest request, HttpServletResponse response) {
        String email = request.getEmail();
        String rawPassword = request.getPassword();

        // 1. Basic null/empty validation
        if (email == null || email.isBlank() || rawPassword == null || rawPassword.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body(new LoginResponse("",  "", "","Email and password must be provided"));
        }

        // 2. Find user by email
        Optional<User> userOpt = usersRepo.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new LoginResponse("",  "", "","User does not exist"));
        }

        User user = userOpt.get();

        // 3. Ensure account is verified
        if (user.getVerificationStatus() != VerificationStatus.VERIFIED) {
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new LoginResponse("",  "", "","Email not verified"));
        }

        // 4. Check password
        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            return ResponseEntity
                    .badRequest()
                    .body(new LoginResponse("",  "", "","Invalid password"));
        }

        // 5. Generate JWT
        String accessToken = jwt.generateJwt(user.getEmail());
        String refreshToken = jwt.generateRefreshToken(user.getEmail());

        user.setRefreshToken(refreshToken);
        usersRepo.save(user);

        Cookie cookie = new Cookie("refreshToken",refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(7 * 24 * 3600);
        cookie.setPath("/");
        response.addCookie(cookie);

        return ResponseEntity.ok(
                new LoginResponse(accessToken,user.getName(), user.getEmail(), "Login successful")
        );
    }


    @Override
    public ResponseEntity<RegisterResponse> register(RegisterRequest request) {
        try {
            if (!request.getConfPass().equals(request.getPassword())) {
                return ResponseEntity
                        .badRequest()
                        .body(new RegisterResponse("Passwords do not match", "Error"));
            }

            if (usersRepo.findByEmail(request.getEmail()).isPresent()) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(new RegisterResponse("Email already registered", "Error"));
            }
            String otp = otpService.generateOtp();
            otpService.sendMail(request.getEmail(), otp);

            String passHash = passwordEncoder.encode(request.getPassword());

            User newUser = new User();
            newUser.setName(request.getName());
            newUser.setEmail(request.getEmail());
            newUser.setPasswordHash(passHash);
            newUser.setOtp(otp);
            newUser.setExpiryTime(Instant.now().plus(Duration.ofMinutes(10)));
            newUser.setVerificationStatus(VerificationStatus.UNVERIFIED);

            usersRepo.save(newUser);

            return ResponseEntity.status(HttpStatus.OK).body(new RegisterResponse("Otp Sent Successfully to user email","Success"));
        } catch (Exception e) {
            return new ResponseEntity<>(new RegisterResponse("Error : " + e.getMessage(), "Error"), HttpStatus.BAD_REQUEST);
        }

    }

    @Override
    public ResponseEntity<RegisterResponse> verifyOtp(String email, Integer numOtp) {
        String otp = String.valueOf(numOtp);
        Optional<User> userOpt = usersRepo.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new RegisterResponse("User not found", "Error"));
        }

        User user = userOpt.get();


        if (!user.getOtp().equals(otp) ) {
            return ResponseEntity
                    .badRequest()
                    .body(new RegisterResponse("Invalid OTP", "Failed"));
        }

        if( user.getExpiryTime().isBefore(Instant.now())){
            user.setOtp(null);
            user.setExpiryTime(null);
            return ResponseEntity
                    .badRequest()
                    .body(new RegisterResponse("Invalid OTP", "Failed"));
        }


        user.setOtp(null);
        user.setExpiryTime(null);
        user.setVerificationStatus(VerificationStatus.VERIFIED);
        usersRepo.save(user);

        return ResponseEntity
                .ok(new RegisterResponse("User registered successfully", "Success"));
    }

    @Override
    public ResponseEntity<LoginResponse> getAccessToken(HttpServletResponse response, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new LoginResponse("", "", "",  "No refresh token"));
        }
        String refreshToken = Arrays.stream(cookies).filter(c -> c.getName().equals("refreshToken")).map(Cookie::getValue).findFirst().orElse(null);

        if(refreshToken.isBlank()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new LoginResponse("","","",""));
        }

        String email = jwt.extractEmail(refreshToken);
        if(!(jwt.isRefreshTokenValid(refreshToken,email))){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new LoginResponse("","","","RefreshTokenInvalid"));

        }
        Optional<User> userOptional = usersRepo.findByEmail(email);
        if(userOptional.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new LoginResponse("","","","User Not Found..."));

        }
        User user = userOptional.get();
        String accessToken = jwt.generateJwt(email);
        String newRefreshToken = jwt.generateRefreshToken(email);

        user.setRefreshToken(newRefreshToken);
        usersRepo.save(user);

        Cookie newCookie = new Cookie("refreshToken",newRefreshToken);
        newCookie.setHttpOnly(true);
        newCookie.setSecure(true);
        newCookie.setMaxAge(7 * 24 * 3600);
        newCookie.setPath("/");
        response.addCookie(newCookie);

        return ResponseEntity.status(HttpStatus.OK).body(new LoginResponse(accessToken,user.getName(), user.getEmail(),"Success"));

    }

    @Override
    public ResponseEntity<String> logout(String email) {
        Optional<User> userOptional = usersRepo.findByEmail(email);
        if(userOptional.isEmpty()) return ResponseEntity.badRequest().body("Email Not Found");
        User user = userOptional.get();
        user.setRefreshToken(null);
        usersRepo.save(user);
        return ResponseEntity.ok("Logged out Successfully...");
    }

}
