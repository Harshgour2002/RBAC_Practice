package com.example.CVRUK_backend.authentication.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.CVRUK_backend.authentication.entity.user;

import lombok.Getter;

@Getter
public class CustomUserDetails implements UserDetails {

    private final UUID userId;
    private final String username;
    private final String password;
    private final boolean active;
    private final Set<String> roles;
    private final Set<String> permissions;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(user user) {
        this.userId = user.getId();
        this.username = user.getEmail();
        this.password = user.getPassword();
        this.active = user.isActive();
        this.roles = user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet());
        this.permissions = user.getRoles().stream()
                .flatMap(r -> r.getPermissions().stream())
                .map(p -> p.getName())
                .collect(Collectors.toSet());

        Set<GrantedAuthority> auths = new HashSet<>();
        roles.forEach(role -> auths.add(new SimpleGrantedAuthority("ROLE_" + role)));
        permissions.forEach(permission -> auths.add(new SimpleGrantedAuthority(permission)));
        this.authorities = auths;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
