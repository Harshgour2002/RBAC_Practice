package com.example.CVRUK_backend.authentication.dto;

import jakarta.validation.constraints.NotBlank;

public record refreshToken(
    @NotBlank String refreshToken
) {    
}
