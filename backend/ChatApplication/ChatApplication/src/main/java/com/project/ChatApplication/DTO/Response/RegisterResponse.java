package com.project.ChatApplication.DTO.Response;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@AllArgsConstructor
public class RegisterResponse {
    private String message;
    private String status;
}
