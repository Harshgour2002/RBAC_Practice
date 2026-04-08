package com.example.rbac.service;

import com.example.rbac.audit.AuditService;
import com.example.rbac.caching.TokenBlacklistService;
import com.example.rbac.dto.AuthDtos;
import com.example.rbac.exception.BadRequestException;
import com.example.rbac.exception.UnauthorizedException;
import com.example.rbac.metrics.SecurityMetrics;
import com.example.rbac.model.AuditEventType;
import com.example.rbac.model.RefreshToken;
import com.example.rbac.model.Role;
import com.example.rbac.model.User;
import com.example.rbac.repository.RefreshTokenRepository;
import com.example.rbac.repository.RoleRepository;
import com.example.rbac.repository.UserRepository;
import com.example.rbac.security.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final TokenBlacklistService blacklistService;
    private final AuditService auditService;
    private final SecurityMetrics securityMetrics;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository,
                       RefreshTokenRepository refreshTokenRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, TokenBlacklistService blacklistService,
                       AuditService auditService, SecurityMetrics securityMetrics) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.blacklistService = blacklistService;
        this.auditService = auditService;
        this.securityMetrics = securityMetrics;
    }

    @Transactional
    public void register(AuthDtos.RegisterRequest req) {
        if (userRepository.findByEmail(req.email()).isPresent()) throw new BadRequestException("Email already in use");
        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_USER").build()));
        User user = User.builder().email(req.email()).passwordHash(passwordEncoder.encode(req.password()))
                .createdAt(Instant.now()).enabled(true).roles(new HashSet<>()).build();
        user.getRoles().add(defaultRole);
        userRepository.save(user);
    }

    @Transactional
    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest req) {
        User user = userRepository.findByEmail(req.email()).orElseThrow(() -> new UnauthorizedException("Invalid credentials"));
        if (!passwordEncoder.matches(req.password(), user.getPasswordHash())) {
            securityMetrics.loginFailures.increment();
            auditService.log(user.getId(), AuditEventType.LOGIN_FAILED, "Bad password");
            throw new UnauthorizedException("Invalid credentials");
        }
        String access = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String refresh = jwtService.generateRefreshToken(user.getId(), user.getEmail());
        refreshTokenRepository.save(RefreshToken.builder().user(user).token(refresh).expiryAt(Instant.now().plusSeconds(604800)).revoked(false).build());
        auditService.log(user.getId(), AuditEventType.USER_LOGIN, "Login successful");
        return new AuthDtos.AuthResponse(access, refresh, jwtService.getAccessTokenTtlSeconds());
    }

    @Transactional
    public AuthDtos.AuthResponse refresh(AuthDtos.RefreshRequest req) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(req.refreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));
        if (refreshToken.isRevoked() || refreshToken.getExpiryAt().isBefore(Instant.now())) throw new UnauthorizedException("Refresh token expired/revoked");
        Claims claims = jwtService.parseClaims(req.refreshToken());
        if (!"refresh".equals(claims.get("type", String.class))) throw new UnauthorizedException("Invalid token type");
        refreshToken.setRevoked(true);
        String access = jwtService.generateAccessToken(refreshToken.getUser().getId(), refreshToken.getUser().getEmail());
        String newRefresh = jwtService.generateRefreshToken(refreshToken.getUser().getId(), refreshToken.getUser().getEmail());
        refreshTokenRepository.save(RefreshToken.builder().user(refreshToken.getUser()).token(newRefresh).expiryAt(Instant.now().plusSeconds(604800)).revoked(false).build());
        return new AuthDtos.AuthResponse(access, newRefresh, jwtService.getAccessTokenTtlSeconds());
    }

    @Transactional
    public void logout(AuthDtos.LogoutRequest req) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(req.refreshToken())
                .orElseThrow(() -> new UnauthorizedException("Invalid refresh token"));
        refreshToken.setRevoked(true);
        blacklistService.blacklist(req.refreshToken(), Duration.between(Instant.now(), refreshToken.getExpiryAt()));
        auditService.log(refreshToken.getUser().getId(), AuditEventType.USER_LOGOUT, "User logout");
    }
}
