package com.example.rbac.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    private final SecretKey key;
    private final long accessTokenTtlSeconds;
    private final long refreshTokenTtlSeconds;

    public JwtService(@Value("${security.jwt.secret}") String secret,
                      @Value("${security.jwt.access-token-ttl-seconds:1800}") long accessTokenTtlSeconds,
                      @Value("${security.jwt.refresh-token-ttl-seconds:604800}") long refreshTokenTtlSeconds) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenTtlSeconds = accessTokenTtlSeconds;
        this.refreshTokenTtlSeconds = refreshTokenTtlSeconds;
    }

    public String generateAccessToken(Long userId, String email) {
        return buildToken(userId, email, accessTokenTtlSeconds, Map.of("type", "access"));
    }

    public String generateRefreshToken(Long userId, String email) {
        return buildToken(userId, email, refreshTokenTtlSeconds, Map.of("type", "refresh"));
    }

    private String buildToken(Long userId, String email, long ttl, Map<String, Object> claims) {
        Instant now = Instant.now();
        return Jwts.builder()
                .claims(claims)
                .subject(String.valueOf(userId))
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(ttl)))
                .claim("email", email)
                .signWith(key)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public long getAccessTokenTtlSeconds() {
        return accessTokenTtlSeconds;
    }
}
