package org.example.server.statistics.presentation.dto.res;

import java.time.LocalDate;
import java.util.List;

public record CalendarResponse(
    int year,
    int month,
    long totalFocusedSeconds,
    int completedCupCount,
    BestDay bestDay,
    List<DayStat> days
) {

    public static CalendarResponse of(
        int year,
        int month,
        long totalFocusedSeconds,
        int completedCupCount,
        BestDay bestDay,
        List<DayStat> days
    ) {
        return new CalendarResponse(
            year,
            month,
            totalFocusedSeconds,
            completedCupCount,
            bestDay,
            days
        );
    }

    public record DayStat(
        LocalDate date,
        int intensityLevel
    ) {}

    public record BestDay(
        LocalDate date,
        long focusedSeconds,
        int completedCupCount
    ) {}
}