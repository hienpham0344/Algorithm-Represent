package com.example.main.service;

import com.example.main.dto.request.UserReportRequest;
import com.example.main.dto.response.UserReportResponse;
import com.example.main.entity.UserEntity;
import com.example.main.entity.UserReportEntity;
import com.example.main.repository.ReportRepository;
import com.example.main.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Lưu và truy vấn lịch sử report từ cơ sở dữ liệu (bảng user_reports).
 */
@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    public ReportService(ReportRepository reportRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public UserReportResponse create(UserReportRequest request) {
        UserReportEntity entity = new UserReportEntity();
        if (request.userId() != null) {
            UserEntity user = userRepository.findById(request.userId()).orElse(null);
            entity.setUser(user);
        }
        entity.setModule(request.module());
        entity.setContent(request.content());
        return reportRepository.save(entity).toResponse();
    }

    /** Toàn bộ lịch sử (dùng cho admin). */
    @Transactional(readOnly = true)
    public List<UserReportResponse> findAll() {
        return reportRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(UserReportEntity::toResponse)
                .toList();
    }

    /** Lịch sử report của riêng một người dùng. */
    @Transactional(readOnly = true)
    public List<UserReportResponse> findByUser(long userId) {
        return reportRepository.findByUser_IdOrderByCreatedAtDesc(userId).stream()
                .map(UserReportEntity::toResponse)
                .toList();
    }
}
