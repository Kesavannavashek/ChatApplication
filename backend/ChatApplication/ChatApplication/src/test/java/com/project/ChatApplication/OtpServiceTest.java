package com.project.ChatApplication;

import com.project.ChatApplication.EmailUtils.EmailGeneratorAndSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class OtpServiceTest {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private EmailGeneratorAndSender otpService;

    @Test
    void testGenerateOtpLength() {
        String otp = otpService.generateOtp();
        assertEquals(6, otp.length(), "OTP should always be 6 digits long");
    }

    @Test
    void testGenerateOtpIsNumeric() {
        String otp = otpService.generateOtp();
        assertTrue(otp.matches("\\d{6}"), "OTP should only contain digits");
    }

    @Test
    void testGenerateOtpRange() {
        int otpValue = Integer.parseInt(otpService.generateOtp());
        assertTrue(otpValue >= 100000 && otpValue <= 999999, "OTP must be between 100000 and 999999");
    }

    @Test
    void testGenerateOtpUniqueness() {
        String otp1 = otpService.generateOtp();
        String otp2 = otpService.generateOtp();
        assertNotEquals(otp1, otp2, "Two OTPs should not be equal (most of the time)");
    }


    @Test
    void testSendRealEmail() {
        otpService.sendMail("Kesavanmathiyazhagan@gmail.com", "654321");
        System.out.println("Check your inbox for the OTP email!");
    }
}
