package com.example.main.entity;

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
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

/**
 * Ghi chú của người dùng theo từng module (Sorting, Stack, Queue, ...).
 * Mỗi (user, module) chỉ có một ghi chú, có thể chỉnh sửa.
 */
@Entity
@Table(
        name = "user_module_notes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_module_note",
                        columnNames = {"user_id", "module"}
                )
        },
        indexes = {
                @Index(name = "idx_user_module_notes_user", columnList = "user_id")
        }
)
public class UserModuleNoteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(name = "module", nullable = false, length = 100)
    private String module;

    @Column(nullable = false, columnDefinition = "nvarchar(max)")
    private String content;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @jakarta.persistence.PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @jakarta.persistence.PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
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

    public Instant getUpdatedAt() {
        return updatedAt;
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
}
