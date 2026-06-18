package com.example.main.repository;

import com.example.main.entity.UserNoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<UserNoteEntity, Long> {
    Optional<UserNoteEntity> findByUser_IdAndAlgorithm_Id(long userId, long algorithmId);

    List<UserNoteEntity> findByUser_IdOrderByUpdatedAtDesc(long userId);

    List<UserNoteEntity> findByAlgorithm_IdOrderByUpdatedAtDesc(long algorithmId);
}
