package org.example.server.statistics.presentation.dto.res;

import java.time.LocalDate;

public record StatisticsResponse(
    String period,
    long totalFocusedSeconds,
    long totalFocusedHours,
    int currentStreakDays,
    int longestStreakDays,
    long completedCupCount,
    long completedCycleCount
) {

    public static  StatisticsResponse of(
        String resolvedPeriod,
        long totalFocusedSeconds,
        long totalFocusedHours,
        int currentStreakDays,
        int longestStreakDays,
        long completedCupCount,
        long completedCycleCount){
        return new StatisticsResponse(
            resolvedPeriod,
            totalFocusedSeconds,
            totalFocusedHours,
            currentStreakDays,
            longestStreakDays,
            completedCupCount,
            completedCycleCount
        );
    }
}
