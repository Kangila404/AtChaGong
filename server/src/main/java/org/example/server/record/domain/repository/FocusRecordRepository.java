package org.example.server.record.domain.repository;

import java.util.Optional;
import org.example.server.record.domain.models.FocusRecord;

public interface FocusRecordRepository {
    Optional<FocusRecord> findByUserId(Long userId);
}
