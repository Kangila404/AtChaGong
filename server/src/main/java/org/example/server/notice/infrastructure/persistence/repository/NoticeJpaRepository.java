package org.example.server.notice.infrastructure.persistence.repository;

import java.util.Optional;
import org.example.server.notice.domain.enums.NoticeStatus;
import org.example.server.notice.domain.models.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeJpaRepository extends JpaRepository<Notice, Long> {
    Page<Notice> findByStatus(NoticeStatus status, Pageable pageable);
    Optional<Notice> findByIdAndStatus(Long id, NoticeStatus status);
}
