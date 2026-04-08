package com.example.rbac.service;

import com.example.rbac.model.User;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserEntityBuilderTest {
    @Test
    void shouldInitializeRolesWhenBuiltWithLombokBuilder() {
        User user = User.builder()
                .email("user@test.com")
                .passwordHash("hash")
                .enabled(true)
                .createdAt(Instant.now())
                .build();

        assertNotNull(user.getRoles());
    }
}
