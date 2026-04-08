package com.example.CVRUK_backend.authentication.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.CVRUK_backend.authentication.entity.permission;

@Repository
public interface permissionRepo extends JpaRepository<permission, Long> {
    Optional<permission> findByName(String name);   
}
