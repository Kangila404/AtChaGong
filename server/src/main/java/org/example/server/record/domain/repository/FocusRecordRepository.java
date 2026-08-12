package org.example.server.record.domain.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.example.server.record.domain.models.FocusRecord;

public interface FocusRecordRepository {
    Optional<FocusRecord> findByUserId(Long userId);
    List<FocusRecord> findByUserIdAndFocusedDate(Long userId, LocalDate focusedDate);
    boolean existsByUserIdAndStartedAt(Long userId, LocalDateTime startedAt);
    FocusRecord save(FocusRecord focusRecord);
    List<FocusRecord> findAllByUserId(Long userId);
    List<FocusRecord> findAllByUserIdAndFocusedDateBetween(Long userId, LocalDate startDate, LocalDate endDate);
    List<FocusRecord> findAllByUserIdAndFocusedDate(Long userId, LocalDate focusedDate);
}
