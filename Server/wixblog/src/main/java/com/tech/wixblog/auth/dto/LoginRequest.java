package com.tech.wixblog.auth.dto;

public record LoginRequest(

        String email,
        String password
) {}