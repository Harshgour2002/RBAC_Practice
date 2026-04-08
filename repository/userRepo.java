package com.example.CVRUK_backend.authentication.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.CVRUK_backend.authentication.entity.user;


@Repository

public interface userRepo extends JpaRepository<user, UUID> {
    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<user> findByEmail(String email);

    boolean existsByEmail(String email);
    
}
