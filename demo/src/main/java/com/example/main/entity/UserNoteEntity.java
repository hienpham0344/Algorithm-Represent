package com.example.main.entity;

import com.example.main.dto.request.UserNoteRequest;
import com.example.main.dto.response.UserNoteResponse;
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
        name = "user_notes",
        uniqueConstraints = {
                @jakarta.persistence.UniqueConstraint(
                        name = "uk_user_note_user_algorithm",
                        columnNames = {"user_id", "algorithm_id"}
                )
        },
        indexes = {
                @Index(name = "idx_user_notes_user_algorithm", columnList = "user_id,algorithm_id")
        }
)
public class UserNoteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "algorithm_id", nullable = false)
    private AlgorithmEntity algorithm;

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

    public UserNoteResponse toResponse() {
        return new UserNoteResponse(
                id == null ? 0L : id,
                user == null ? 0L : user.getId(),
                algorithm == null ? 0L : algorithm.getId(),
                content,
                createdAt,
                updatedAt
        );
    }

    public static UserNoteEntity fromRequest(UserNoteRequest request, UserEntity user, AlgorithmEntity algorithm) {
        UserNoteEntity entity = new UserNoteEntity();
        entity.user = user;
        entity.algorithm = algorithm;
        entity.content = request.content();
        return entity;
    }

    public Long getId() {
        return id;
    }

    public UserEntity getUser() {
        return user;
    }

    public AlgorithmEntity getAlgorithm() {
        return algorithm;
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

    public void setAlgorithm(AlgorithmEntity algorithm) {
        this.algorithm = algorithm;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
