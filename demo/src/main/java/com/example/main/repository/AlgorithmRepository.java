package com.example.main.repository;

import com.example.main.entity.AlgorithmEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlgorithmRepository extends JpaRepository<AlgorithmEntity, Long> {
    Optional<AlgorithmEntity> findByAlgorithmCode(String algorithmCode);

    boolean existsByAlgorithmCodeIgnoreCase(String algorithmCode);

    List<AlgorithmEntity> findAllByOrderByNameAsc();
}
