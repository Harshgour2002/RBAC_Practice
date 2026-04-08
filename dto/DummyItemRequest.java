package com.example.CVRUK_backend.authentication.dto;

import jakarta.validation.constraints.NotBlank;

public record DummyItemRequest(
        @NotBlank(message = "title is required")
        String title,
        String description) {
}
