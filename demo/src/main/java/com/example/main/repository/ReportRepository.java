package com.example.main.repository;

import com.example.main.entity.UserReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<UserReportEntity, Long> {
    List<UserReportEntity> findAllByOrderByCreatedAtDesc();

    List<UserReportEntity> findByUser_IdOrderByCreatedAtDesc(long userId);
}
