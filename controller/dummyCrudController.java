package com.example.CVRUK_backend.authentication.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.CVRUK_backend.authentication.dto.ApiResponse;
import com.example.CVRUK_backend.authentication.dto.DummyItemRequest;
import com.example.CVRUK_backend.authentication.dto.DummyItemResponse;
import com.example.CVRUK_backend.authentication.service.dummyCrudService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/dummy-items")
@RequiredArgsConstructor
@Validated
public class dummyCrudController {

    private final dummyCrudService dummyCrudService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<DummyItemResponse> create(@Valid @RequestBody DummyItemRequest request,
            Authentication authentication) {
        return ApiResponse.<DummyItemResponse>builder()
                .success(true)
                .message("Dummy item created")
                .data(dummyCrudService.create(request, authentication.getName()))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ApiResponse<List<DummyItemResponse>> getAll() {
        return ApiResponse.<List<DummyItemResponse>>builder()
                .success(true)
                .message("Dummy items fetched")
                .data(dummyCrudService.getAll())
                .build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ApiResponse<DummyItemResponse> getById(@PathVariable Long id) {
        return ApiResponse.<DummyItemResponse>builder()
                .success(true)
                .message("Dummy item fetched")
                .data(dummyCrudService.getById(id))
                .build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<DummyItemResponse> update(@PathVariable Long id, @Valid @RequestBody DummyItemRequest request) {
        return ApiResponse.<DummyItemResponse>builder()
                .success(true)
                .message("Dummy item updated")
                .data(dummyCrudService.update(id, request))
                .build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        dummyCrudService.delete(id);
        return ApiResponse.<Void>builder()
                .success(true)
                .message("Dummy item deleted")
                .build();
    }

    @GetMapping("/user-only")
    @PreAuthorize("hasRole('USER') and !hasRole('ADMIN')")
    public ApiResponse<String> userOnly() {
        return ApiResponse.<String>builder()
                .success(true)
                .message("Visible only to USER role")
                .data("USER_ONLY_OK")
                .build();
    }
}
