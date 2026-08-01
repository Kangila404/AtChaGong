package org.example.server.admin.infrastructure.persistence.repository;

import org.example.server.admin.domain.models.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeJpaRepository extends JpaRepository<Notice,Long> {

}
