package com.example.main.repository;

import com.example.main.entity.UserSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<UserSessionEntity, Long> {
    Optional<UserSessionEntity> findBySessionToken(String sessionToken);

    List<UserSessionEntity> findByUser_IdOrderByCreatedAtDesc(Long userId);
}
