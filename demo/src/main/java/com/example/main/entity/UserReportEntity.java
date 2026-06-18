package com.example.main.entity;

import com.example.main.dto.response.UserReportResponse;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(
        name = "user_reports",
        indexes = {
                @Index(name = "idx_user_reports_user", columnList = "user_id"),
                @Index(name = "idx_user_reports_created_at", columnList = "created_at")
        }
)
public class UserReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "module", length = 100)
    private String module;

    @Column(nullable = false, columnDefinition = "nvarchar(max)")
    private String content;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @jakarta.persistence.PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public UserReportResponse toResponse() {
        return new UserReportResponse(
                id == null ? 0L : id,
                user == null ? 0L : user.getId(),
                user == null ? "(không rõ)" : user.getUsername(),
                module,
                content,
                createdAt
        );
    }

    public Long getId() {
        return id;
    }

    public UserEntity getUser() {
        return user;
    }

    public String getModule() {
        return module;
    }

    public String getContent() {
        return content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
