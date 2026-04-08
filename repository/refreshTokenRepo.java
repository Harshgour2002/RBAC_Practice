package com.example.CVRUK_backend.authentication.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.CVRUK_backend.authentication.entity.RefreshToken;
import com.example.CVRUK_backend.authentication.entity.user;

@Repository
public interface refreshTokenRepo extends JpaRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(user user);
}
