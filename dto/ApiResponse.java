package com.example.CVRUK_backend.authentication.dto;

import lombok.Builder;

@Builder
public record ApiResponse<T>(boolean success, String message, String error, T data) {
} 
