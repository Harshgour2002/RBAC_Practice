package com.example.rbac.service;

import com.example.rbac.security.JwtService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtServiceTest {
    @Test
    void shouldGenerateAndParseAccessToken() {
        JwtService jwtService = new JwtService("change-this-secret-to-a-long-random-key-change-this-secret", 1800, 604800);
        String token = jwtService.generateAccessToken(11L, "u@test.com");
        assertEquals("11", jwtService.parseClaims(token).getSubject());
    }
}
