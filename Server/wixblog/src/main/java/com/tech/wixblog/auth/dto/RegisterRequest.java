package com.tech.wixblog.auth.dto;

import com.tech.wixblog.common.validation.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Size(max = 255, message = "Email must not exceed 255 characters")
        String email,

        @NotBlank(message = "Username is required")
        @Size(
                min = 3,
                max = 30,
                message = "Username must be between 3 and 30 characters"
        )
        String username,

        @NotBlank(message = "Password is required")
        @Size(
                min = 8,
                max = 72,
                message = "Password must be between 8 and 72 characters"
        )
        @StrongPassword
        String password
) {}