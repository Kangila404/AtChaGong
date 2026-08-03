package org.example.server.record.infrastructure.persistence.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.example.server.record.domain.models.FocusRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FocusRecordJpaRepository extends JpaRepository<FocusRecord,Long> {

    Optional<FocusRecord> findByUserId(Long userId);
    boolean existsByUserIdAndStartedAt(Long userId, LocalDateTime startedAt);
}
