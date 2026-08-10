package org.example.server.statistics.presentation.dto.res;

import java.time.LocalDate;
import java.util.List;

public record CalendarResponse(
    int year,
    int month,
    long totalFocusedSeconds,
    int completedCupCount,
    int completedCycleCount,
    List<DayStat> days
) {
    public record DayStat(
        LocalDate date,
        long focusedSeconds,
        int completedCupCount,
        int completedCycleCount,
        boolean cycleAchieved
    ) {}
}
