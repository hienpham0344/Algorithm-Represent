package com.example.main.repository;

import com.example.main.entity.UserModuleNoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ModuleNoteRepository extends JpaRepository<UserModuleNoteEntity, Long> {
    Optional<UserModuleNoteEntity> findByUser_IdAndModule(long userId, String module);
}
