package org.example.server.notice.domain.repository;

import java.util.Optional;
import org.example.server.notice.domain.models.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeRepository {
    Page<Notice> findAll(Pageable pageable);
    Optional<Notice> findById(Long id);
    Notice save(Notice notice);
    void delete(Notice notice);
}
