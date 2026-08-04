package org.example.server.record.presentation.dto.res;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import org.example.server.record.domain.models.FocusRecord;

public record FocusRecordResponse(
    Long focusRecordId,
    Long beverageId,
    int focusMinutes,
    int focusedSeconds,
    OffsetDateTime startedAt,
    OffsetDateTime completedAt,
    LocalDate focusedDate
) {
    public static FocusRecordResponse of(FocusRecord focusRecord, OffsetDateTime startedAt, OffsetDateTime completedAt) {
        Long beverageId = focusRecord.getBeverage() == null ? null : focusRecord.getBeverage().getId();
        return new FocusRecordResponse(
            focusRecord.getId(),
            beverageId,
            focusRecord.getFocusMinutes(),
            focusRecord.getFocusedSeconds(),
            startedAt,
            completedAt,
            focusRecord.getFocusedDate()
        );
    }
}
