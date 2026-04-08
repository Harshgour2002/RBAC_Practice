package com.example.CVRUK_backend.authentication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.CVRUK_backend.authentication.dto.ApiResponse;
import com.example.CVRUK_backend.authentication.dto.AuthResponse;
import com.example.CVRUK_backend.authentication.dto.loginRequest;
import com.example.CVRUK_backend.authentication.dto.refreshToken;
import com.example.CVRUK_backend.authentication.dto.signupRequest;
import com.example.CVRUK_backend.authentication.service.authService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class authController {

    private final authService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<AuthResponse>> signup(@Valid @RequestBody signupRequest request) {
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Signup successful")
                .data(authService.signup(request))
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody loginRequest request) {
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Login successful")
                .data(authService.login(request))
                .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody refreshToken request) {
        return ResponseEntity.ok(ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Token refreshed")
                .data(authService.refreshToken(request))
                .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody refreshToken request) {
        authService.logout(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Logged out")
                .build());
    }
}
