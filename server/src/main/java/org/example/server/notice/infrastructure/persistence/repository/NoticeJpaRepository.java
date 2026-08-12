package org.example.server.notice.infrastructure.persistence.repository;

import org.example.server.notice.domain.models.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeJpaRepository extends JpaRepository<Notice, Long> {

}
