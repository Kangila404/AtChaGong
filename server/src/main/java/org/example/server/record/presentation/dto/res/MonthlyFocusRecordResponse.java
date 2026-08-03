package org.example.server.record.presentation.dto.res;

import java.time.LocalDate;
import java.util.List;

public record MonthlyFocusRecordResponse(
    int year,
    int month,
    int totalFocusedSeconds,
    int completedCupCount,
    int completedCycleCount,
    List<MonthlyDay> days
) {
    public static MonthlyFocusRecordResponse of(
        int year,
        int month,
        List<MonthlyDay> days,
        int totalFocusedSeconds,
        int completedCupCount,
        int completedCycleCount
    ) {
        return new MonthlyFocusRecordResponse(
            year,
            month,
            totalFocusedSeconds,
            completedCupCount,
            completedCycleCount,
            days
        );
    }

    public record MonthlyDay(
        LocalDate date,
        int totalFocusedSeconds,
        int completedCupCount,
        int completedCycleCount,
        boolean cycleAchieved
    ) {
        public static MonthlyDay of(
            LocalDate date,
            int totalFocusedSeconds,
            int completedCupCount,
            int completedCycleCount
        ) {
            return new MonthlyDay(
                date,
                totalFocusedSeconds,
                completedCupCount,
                completedCycleCount,
                completedCycleCount >= 1
            );
        }
    }
}
