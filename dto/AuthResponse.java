package com.example.CVRUK_backend.authentication.dto;

import java.util.Set;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    long expiry,
    Set<String> roles,
    Set<String> persmission
) {
} 
