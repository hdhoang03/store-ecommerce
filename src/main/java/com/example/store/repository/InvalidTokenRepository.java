package com.example.store.repository;

import com.example.store.entity.InvalidatedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidTokenRepository extends JpaRepository<InvalidatedToken, String> {
}
