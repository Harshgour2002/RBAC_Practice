package com.example.CVRUK_backend.authentication.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;


public record loginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
) {
}