package com.example.rbac.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().contains("/auth/login");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ip = request.getRemoteAddr();
        Attempt attempt = attempts.computeIfAbsent(ip, i -> new Attempt());
        if (!attempt.allow()) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            return;
        }
        filterChain.doFilter(request, response);
    }

    private static class Attempt {
        private int count = 0;
        private Instant windowStart = Instant.now();
        boolean allow() {
            if (Instant.now().isAfter(windowStart.plusSeconds(60))) {
                count = 0;
                windowStart = Instant.now();
            }
            count++;
            return count <= 30;
        }
    }
}
