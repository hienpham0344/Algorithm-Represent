package com.example.main.entity;

import com.example.main.dto.request.AlgorithmRequest;
import com.example.main.dto.response.AlgorithmResponse;
import com.example.main.entity.converter.JsonMapConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name = "algorithms")
public class AlgorithmEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "algorithm_code", nullable = false, unique = true, length = 100)
    private String algorithmCode;

    @Column(nullable = false, length = 255)
    private String name;

    @Convert(converter = JsonMapConverter.class)
    @Column(name = "explanation_json", nullable = false, columnDefinition = "nvarchar(max)")
    private Map<String, Object> explanation = new LinkedHashMap<>();

    @Column(name = "time_complexity", nullable = false, length = 255)
    private String timeComplexity;

    @Column(name = "space_complexity", nullable = false, length = 255)
    private String spaceComplexity;

    @Column(length = 255)
    private String memory;

    @Column(name = "overview", columnDefinition = "nvarchar(max)")
    private String overview;

    @Column(length = 100)
    private String stability;

    @Column(length = 100)
    private String category;

    @Column(length = 50)
    private String status;

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
        if (explanation == null) {
            explanation = new LinkedHashMap<>();
        }
    }

    @jakarta.persistence.PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
        if (explanation == null) {
            explanation = new LinkedHashMap<>();
        }
    }

    public AlgorithmResponse toResponse() {
        return new AlgorithmResponse(
                id == null ? 0L : id,
                algorithmCode,
                name,
                new LinkedHashMap<>(explanation),
                timeComplexity,
                spaceComplexity,
                memory,
                overview,
                stability,
                category,
                status,
                createdAt,
                updatedAt
        );
    }

    public static AlgorithmEntity fromRequest(AlgorithmRequest request) {
        AlgorithmEntity entity = new AlgorithmEntity();
        entity.algorithmCode = request.algorithmCode();
        entity.name = request.name();
        entity.explanation = request.explanation() == null ? new LinkedHashMap<>() : new LinkedHashMap<>(request.explanation());
        entity.timeComplexity = request.timeComplexity();
        entity.spaceComplexity = request.spaceComplexity();
        entity.memory = request.memory();
        entity.overview = request.overview();
        entity.stability = request.stability();
        entity.category = request.category();
        entity.status = request.status();
        return entity;
    }

    public Long getId() {
        return id;
    }

    public String getAlgorithmCode() {
        return algorithmCode;
    }

    public String getName() {
        return name;
    }

    public Map<String, Object> getExplanation() {
        return explanation;
    }

    public String getTimeComplexity() {
        return timeComplexity;
    }

    public String getSpaceComplexity() {
        return spaceComplexity;
    }

    public String getMemory() {
        return memory;
    }

    public String getOverview() {
        return overview;
    }

    public String getStability() {
        return stability;
    }

    public String getCategory() {
        return category;
    }

    public String getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAlgorithmCode(String algorithmCode) {
        this.algorithmCode = algorithmCode;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setExplanation(Map<String, Object> explanation) {
        this.explanation = explanation;
    }

    public void setTimeComplexity(String timeComplexity) {
        this.timeComplexity = timeComplexity;
    }

    public void setSpaceComplexity(String spaceComplexity) {
        this.spaceComplexity = spaceComplexity;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setStability(String stability) {
        this.stability = stability;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
