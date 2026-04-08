package com.example.CVRUK_backend.authentication.dto;

import java.time.LocalDateTime;

public record DummyItemResponse(
        Long id,
        String title,
        String description,
        String createdBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
