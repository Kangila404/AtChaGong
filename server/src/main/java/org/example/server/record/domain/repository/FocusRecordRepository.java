package org.example.server.record.domain.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import org.example.server.record.domain.models.FocusRecord;

public interface FocusRecordRepository {
    Optional<FocusRecord> findByUserId(Long userId);
    boolean existsByUserIdAndStartedAt(Long userId, LocalDateTime startedAt);
    FocusRecord save(FocusRecord focusRecord);
}
