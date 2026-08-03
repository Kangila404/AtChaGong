package org.example.server.record.presentation.dto.res;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import org.example.server.record.domain.models.FocusRecord;

public record DailyFocusRecordResponse(
    LocalDate date,
    int totalFocusedSeconds,
    int completedCupCount,
    int completedCycleCount,
    boolean cycleAchieved,
    List<DailyRecord> records
) {
    public static DailyFocusRecordResponse of(
        LocalDate date,
        List<DailyRecord> records,
        int totalFocusedSeconds,
        int completedCycleCount
    ) {
        return new DailyFocusRecordResponse(
            date,
            totalFocusedSeconds,
            records.size(),
            completedCycleCount,
            completedCycleCount >= 1,
            records
        );
    }

    public record DailyRecord(
        Long focusRecordId,
        Long beverageId,
        int focusMinutes,
        int focusedSeconds,
        OffsetDateTime startedAt,
        OffsetDateTime completedAt
    ) {
        public static DailyRecord of(FocusRecord focusRecord, OffsetDateTime startedAt, OffsetDateTime completedAt) {
            Long beverageId = focusRecord.getBeverage() == null ? null : focusRecord.getBeverage().getId();
            return new DailyRecord(
                focusRecord.getId(),
                beverageId,
                focusRecord.getFocusMinutes(),
                focusRecord.getFocusedSeconds(),
                startedAt,
                completedAt
            );
        }
    }
}
