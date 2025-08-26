package com.project.ChatApplication.EmailUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class EmailGeneratorAndSender {

    @Autowired
    private JavaMailSender mailSender;

    public String generateOtp(){
        SecureRandom secureRandom = new SecureRandom();
        int otp = secureRandom.nextInt(900000) + 100000;
        return String.valueOf(otp);
    }

    public void sendMail(String email, String otp){
        try {
            System.out.println("Mail Sender: "+mailSender);
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Your Verification OTP");
            message.setText("Your OTP is: " + otp + ". It will expire in 10 minutes.");
            mailSender.send(message);
        } catch (Exception e){
            System.out.println("Error: "+ e.getMessage());
        }

    }
}
