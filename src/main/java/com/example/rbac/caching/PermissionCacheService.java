package com.example.rbac.caching;

import com.example.rbac.model.User;
import com.example.rbac.repository.UserRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class PermissionCacheService {
    private static final String PREFIX = "user_permissions:";
    private final StringRedisTemplate redisTemplate;
    private final UserRepository userRepository;
    private final ConcurrentHashMap<Long, Set<String>> localPermissionCache = new ConcurrentHashMap<>();

    public PermissionCacheService(StringRedisTemplate redisTemplate, UserRepository userRepository) {
        this.redisTemplate = redisTemplate;
        this.userRepository = userRepository;
    }

    @CircuitBreaker(name = "redisPermissions", fallbackMethod = "readFromLocal")
    @Retry(name = "redisPermissions")
    @TimeLimiter(name = "redisPermissions")
    public CompletableFuture<Set<String>> getPermissions(long userId) {
        return CompletableFuture.supplyAsync(() -> {
            Set<String> cached = redisTemplate.opsForSet().members(PREFIX + userId);
            if (cached != null && !cached.isEmpty()) {
                localPermissionCache.put(userId, cached);
                return cached;
            }
            Set<String> computed = computeAndWarm(userId);
            localPermissionCache.put(userId, computed);
            return computed;
        });
    }

    public CompletableFuture<Set<String>> readFromLocal(long userId, Throwable throwable) {
        return CompletableFuture.completedFuture(localPermissionCache.getOrDefault(userId, Set.of()));
    }

    public Set<String> computeAndWarm(long userId) {
        User user = userRepository.findWithRolesById(userId).orElseThrow();
        Set<String> permissions = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(p -> p.getName().toUpperCase())
                .collect(Collectors.toSet());
        String key = PREFIX + userId;
        redisTemplate.delete(key);
        if (!permissions.isEmpty()) {
            redisTemplate.opsForSet().add(key, permissions.toArray(String[]::new));
            redisTemplate.expire(key, 15, TimeUnit.MINUTES);
        }
        return permissions;
    }

    public void invalidate(long userId) {
        redisTemplate.delete(PREFIX + userId);
        localPermissionCache.remove(userId);
    }
}
