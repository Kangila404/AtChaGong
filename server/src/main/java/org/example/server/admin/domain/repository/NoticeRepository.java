package org.example.server.admin.domain.repository;

import java.util.Optional;
import org.example.server.admin.domain.models.Notice;

public interface NoticeRepository {
    Optional<Notice> findById(Long id);
}
