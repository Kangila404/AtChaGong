package org.example.server.record.infrastructure.persistence.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.example.server.record.domain.models.FocusRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FocusRecordJpaRepository extends JpaRepository<FocusRecord,Long> {

    Optional<FocusRecord> findByUserId(Long userId);
    List<FocusRecord> findByUserIdAndFocusedDateOrderByStartedAtAsc(Long userId, LocalDate focusedDate);
    boolean existsByUserIdAndStartedAt(Long userId, LocalDateTime startedAt);
}
