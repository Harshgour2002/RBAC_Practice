package com.example.rbac.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDtos {
    public record RegisterRequest(@Email String email, @NotBlank String password) {}
    public record LoginRequest(@Email String email, @NotBlank String password) {}
    public record RefreshRequest(@NotBlank String refreshToken) {}
    public record LogoutRequest(@NotBlank String refreshToken) {}
    public record AuthResponse(String accessToken, String refreshToken, long expiresInSeconds) {}
}
