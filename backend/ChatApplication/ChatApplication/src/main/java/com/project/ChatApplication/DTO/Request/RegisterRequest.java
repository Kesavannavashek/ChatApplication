package com.project.ChatApplication.DTO.Request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
    private String confPass;
}
