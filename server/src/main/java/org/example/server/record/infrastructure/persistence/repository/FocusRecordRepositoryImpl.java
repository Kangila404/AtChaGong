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
    public List<FocusRecord> findByUserIdAndFocusedDateBetween(Long userId, LocalDate startDate,
        LocalDate endDate) {
        return focusRecordJpaRepository.findByUserIdAndFocusedDateBetweenOrderByFocusedDateAscStartedAtAsc(
            userId,
            startDate,
            endDate
        );
    }

    @Override
    public boolean existsByUserIdAndStartedAt(Long userId, LocalDateTime startedAt) {
        return focusRecordJpaRepository.existsByUserIdAndStartedAt(userId, startedAt);
    }

    @Override
    public FocusRecord save(FocusRecord focusRecord) {
        return focusRecordJpaRepository.save(focusRecord);
    }

    @Override
    public List<FocusRecord> findAllByUserId(Long userId) {
        return focusRecordJpaRepository.findAllByUserId(userId);
    }

    @Override
    public List<FocusRecord> findAllByUserIdAndFocusedDateBetween(Long userId, LocalDate startDate, LocalDate endDate) {
        return focusRecordJpaRepository.findAllByUserIdAndFocusedDateBetween(userId, startDate, endDate);
    }

    @Override
    public List<FocusRecord> findAllByUserIdAndFocusedDate(Long userId, LocalDate focusedDate) {
        return focusRecordJpaRepository.findAllByUserIdAndFocusedDate(userId, focusedDate);
    }
}
