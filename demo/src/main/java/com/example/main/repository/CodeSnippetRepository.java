package com.example.main.repository;

import com.example.main.entity.CodeSnippetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CodeSnippetRepository extends JpaRepository<CodeSnippetEntity, Long> {

    List<CodeSnippetEntity> findByAlgorithmCodeOrderByLineOrderAsc(String algorithmCode);

    boolean existsByAlgorithmCode(String algorithmCode);

    void deleteByAlgorithmCode(String algorithmCode);
}
