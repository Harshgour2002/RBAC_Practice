package com.example.CVRUK_backend.authentication.service;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;
import com.example.CVRUK_backend.authentication.entity.RefreshToken;
import com.example.CVRUK_backend.authentication.entity.user;
import com.example.CVRUK_backend.authentication.repository.refreshTokenRepo;
import com.example.CVRUK_backend.common.exception.resourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class refreshTokenService {
    private final refreshTokenRepo refreshTokenRepository;

    @Value("${security.jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpirationMs;

    @Transactional
    public RefreshToken createRefreshToken(user user){
        RefreshToken refreshToken = RefreshToken.builder()
        .user(user)
        .token(UUID.randomUUID().toString())
        .expiryDate(LocalDateTime.now().plus(java.time.Duration.ofMillis(refreshTokenExpirationMs)))
        //.expiryDate(LocalDateTime.now().plusNanos(refreshTokenExpirationMs))
        .build();
        System.out.println("Expiry: " + refreshToken.getExpiryDate());
        System.out.println("Now: " + LocalDateTime.now());

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiry(String token){
    RefreshToken refreshToken = refreshTokenRepository
        .findByToken(token)
        .orElseThrow(() -> new resourceNotFoundException("token not found"));

    System.out.println("Expiry: " + refreshToken.getExpiryDate());
    System.out.println("Now: " + LocalDateTime.now());

    if(refreshToken.getExpiryDate().isBefore(LocalDateTime.now())){
        refreshTokenRepository.delete(refreshToken);
         
    }

    return refreshToken;
}
    
    @Transactional
    public void deleteByUser(user user){
        refreshTokenRepository.deleteByUser(user);
    }

    @Transactional
    public RefreshToken rotateRefreshToken(String token){
        RefreshToken existing = verifyExpiry(token);
        user user = existing.getUser();
        refreshTokenRepository.delete(existing);
        return createRefreshToken(user);
    }

    @Transactional
    public void deleteRefreshToken(String token){
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(() -> new resourceNotFoundException("token not found"));
        refreshTokenRepository.delete(refreshToken);
    }
}
