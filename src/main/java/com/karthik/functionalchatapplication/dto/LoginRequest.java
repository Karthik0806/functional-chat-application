package com.karthik.functionalchatapplication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3,max = 20,message = "username must be at least 3 characters")
    private String username;
    @NotBlank(message = "password is required")
    @Size(min = 6, max = 100,message = "password should be at least 6 characters")
    private String password;
}
