package com.example.main.service;

import com.example.main.entity.UserEntity;
import com.example.main.entity.UserModuleNoteEntity;
import com.example.main.repository.ModuleNoteRepository;
import com.example.main.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Lưu / đọc ghi chú theo module từ cơ sở dữ liệu (bảng user_module_notes).
 */
@Service
public class ModuleNoteService {

    private final ModuleNoteRepository moduleNoteRepository;
    private final UserRepository userRepository;

    public ModuleNoteService(ModuleNoteRepository moduleNoteRepository, UserRepository userRepository) {
        this.moduleNoteRepository = moduleNoteRepository;
        this.userRepository = userRepository;
    }

    /** Trả về nội dung ghi chú của người dùng cho module, hoặc chuỗi rỗng nếu chưa có. */
    @Transactional(readOnly = true)
    public String getNote(long userId, String module) {
        return moduleNoteRepository.findByUser_IdAndModule(userId, module)
                .map(UserModuleNoteEntity::getContent)
                .orElse("");
    }

    /** Tạo mới hoặc cập nhật ghi chú của người dùng cho module. */
    @Transactional
    public void saveNote(long userId, String module, String content) {
        UserModuleNoteEntity entity = moduleNoteRepository
                .findByUser_IdAndModule(userId, module)
                .orElseGet(() -> {
                    UserModuleNoteEntity created = new UserModuleNoteEntity();
                    UserEntity user = userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy user id=" + userId));
                    created.setUser(user);
                    created.setModule(module);
                    return created;
                });
        entity.setContent(content);
        moduleNoteRepository.save(entity);
    }
}
