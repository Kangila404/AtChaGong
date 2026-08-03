package org.example.server.record.infrastructure.persistence.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.server.record.domain.models.FocusRecord;
import org.example.server.record.domain.repository.FocusRecordRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FocusRecordRepositoryImpl implements FocusRecordRepository {

    private final FocusRecordJpaRepository focusRecordJpaRepository;

    @Override
    public Optional<FocusRecord> findByUserId(Long userId) {
        return focusRecordJpaRepository.findByUserId(userId);
    }

    @Override
    public List<FocusRecord> findByUserIdAndFocusedDate(Long userId, LocalDate focusedDate) {
        return focusRecordJpaRepository.findByUserIdAndFocusedDateOrderByStartedAtAsc(userId, focusedDate);
    }

    @Override
    public boolean existsByUserIdAndStartedAt(Long userId, LocalDateTime startedAt) {
        return focusRecordJpaRepository.existsByUserIdAndStartedAt(userId, startedAt);
    }

    @Override
    public FocusRecord save(FocusRecord focusRecord) {
        return focusRecordJpaRepository.save(focusRecord);
    }
}
