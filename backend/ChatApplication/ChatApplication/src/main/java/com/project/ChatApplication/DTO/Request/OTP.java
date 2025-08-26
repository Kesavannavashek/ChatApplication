package com.project.ChatApplication.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OTP {
    private String email;
    private Integer otp;
}
