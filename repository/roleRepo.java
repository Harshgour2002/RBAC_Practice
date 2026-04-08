package com.example.CVRUK_backend.authentication.repository;

import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.CVRUK_backend.authentication.entity.role;
@Repository

public interface roleRepo extends JpaRepository<role, Long>{
    Optional<role> getByName(String name);
    
}
