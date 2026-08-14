package org.example.server.record.presentation.dto.res;

import java.time.LocalDate;

public record DailyFocusRecordResponse(
    LocalDate date,
    int totalFocusedSeconds,
    int completedCupCount
) {
    public static DailyFocusRecordResponse of(
        LocalDate date,
        int totalFocusedSeconds,
        int completedCupCount
    ) {
        return new DailyFocusRecordResponse(date, totalFocusedSeconds, completedCupCount);
    }
}