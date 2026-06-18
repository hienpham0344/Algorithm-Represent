package com.example.main.entity;

import com.example.main.dto.request.UserSessionRequest;
import com.example.main.dto.response.UserSessionResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "user_sessions")
public class UserSessionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "session_token", nullable = false, unique = true, length = 255)
    private String sessionToken;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @jakarta.persistence.PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public UserSessionResponse toResponse() {
        return new UserSessionResponse(
                id == null ? 0L : id,
                user == null ? 0L : user.getId(),
                sessionToken,
                expiresAt,
                revokedAt,
                createdAt
        );
    }

    public static UserSessionEntity fromRequest(UserSessionRequest request, UserEntity user) {
        UserSessionEntity entity = new UserSessionEntity();
        entity.user = user;
        entity.sessionToken = request.sessionToken();
        entity.expiresAt = request.expiresAt();
        return entity;
    }

    public Long getId() {
        return id;
    }

    public UserEntity getUser() {
        return user;
    }

    public String getSessionToken() {
        return sessionToken;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void setSessionToken(String sessionToken) {
        this.sessionToken = sessionToken;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public void setRevokedAt(Instant revokedAt) {
        this.revokedAt = revokedAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
