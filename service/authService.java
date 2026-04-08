package com.example.CVRUK_backend.authentication.service;

import java.util.Set;
import com.example.CVRUK_backend.common.exception.resourceNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.CVRUK_backend.authentication.dto.refreshToken;
import com.example.CVRUK_backend.authentication.dto.AuthResponse;
import com.example.CVRUK_backend.authentication.dto.loginRequest;
import com.example.CVRUK_backend.authentication.dto.signupRequest;
import com.example.CVRUK_backend.authentication.entity.RefreshToken;
import com.example.CVRUK_backend.authentication.entity.role;
import com.example.CVRUK_backend.authentication.entity.user;
import com.example.CVRUK_backend.authentication.enums.enum_roleName;
import com.example.CVRUK_backend.authentication.repository.roleRepo;
import com.example.CVRUK_backend.authentication.repository.userRepo;
import com.example.CVRUK_backend.authentication.security.CustomUserDetails;
import com.example.CVRUK_backend.authentication.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class authService {
    private final roleRepo roleRepository;
    private final userRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService JwtService;
    private final refreshTokenService refreshTokenService;

    @Transactional
    public AuthResponse signup(signupRequest request){
        if(userRepository.existsByEmail(request.email())){
            throw new IllegalArgumentException("email already exists");
        }
        // role defaultRole = roleRepository.getByName(enum_roleName.USER.name())
        //         .orElseThrow(() -> new resourceNotFoundException("default user not found"));
            String roleName = request.role() != null ? request.role() : enum_roleName.USER.name();
            role defaultRole  = roleRepository.getByName(roleName)
            .orElseThrow(() -> new resourceNotFoundException("default role not found"));
                
                user newUser = user.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .isActive(true)
                .roles(Set.of(defaultRole))
                .build();

                user savedUser = userRepository.save(newUser);
                CustomUserDetails userDetails = new CustomUserDetails(savedUser);
                String accessToken = JwtService.generateAccessToken(userDetails);
                RefreshToken refreshToken = refreshTokenService.createRefreshToken(savedUser);

                return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                JwtService.getAccessTokenExpirationMs(),
                userDetails.getRoles(),
                userDetails.getPermissions());
                
    }
    @Transactional
    public AuthResponse login(loginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        user user = userRepository.findById(userDetails.getUserId())
                .orElseThrow(() -> new resourceNotFoundException("User not found"));

        refreshTokenService.deleteByUser(user);
        String accessToken = JwtService.generateAccessToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        log.info("id of user is "+ userDetails.getUserId());

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                JwtService.getAccessTokenExpirationMs(),
                userDetails.getRoles(),
                userDetails.getPermissions());
    }

    @Transactional
    public AuthResponse refreshToken(refreshToken request) {
        RefreshToken rotated = refreshTokenService.rotateRefreshToken(request.refreshToken());
        CustomUserDetails userDetails = new CustomUserDetails(rotated.getUser());
        String accessToken = JwtService.generateAccessToken(userDetails);

        return new AuthResponse(
                accessToken,
                rotated.getToken(),
                "Bearer",
                JwtService.getAccessTokenExpirationMs(),
                userDetails.getRoles(),
                userDetails.getPermissions());
    }

    @Transactional
    public void logout(refreshToken request){
        refreshTokenService.deleteRefreshToken(request.refreshToken());
    }
}
