package org.example.server.statistics.application;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.server.record.domain.models.FocusRecord;
import org.example.server.record.domain.repository.FocusRecordRepository;
import org.example.server.statistics.domain.enums.StatisticsPeriod;
import org.example.server.statistics.exception.StatisticsErrorCode;
import org.example.server.statistics.exception.StatisticsException;
import org.example.server.statistics.presentation.dto.req.StatisticsRequest;
import org.example.server.statistics.presentation.dto.res.CalendarResponse;
import org.example.server.statistics.presentation.dto.res.StatisticsResponse;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.example.server.user.exception.UserErrorCode;
import org.example.server.user.exception.UserException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");
    private static final int MAX_INTENSITY_LEVEL = 4;

    private final UserRepository userRepository;
    private final FocusRecordRepository focusRecordRepository;

    @Transactional(readOnly = true)
    public StatisticsResponse getStatistics(
        String userId,
        StatisticsRequest request
    ) {
        User user = findUserByUserIdOrThrow(userId);
        validateUserStatus(user.getUserStatus());

        StatisticsPeriod resolvedPeriod = resolvePeriod(request.period());

        List<FocusRecord> allRecords =
            findFocusRecordByUserIdOrNull(user.getId());

        List<FocusRecord> filtered = filterByPeriod(allRecords, resolvedPeriod);

        long totalFocusedSeconds = sumFocusedSeconds(filtered);
        long totalFocusedHours = totalFocusedSeconds / 3600;
        int completedCupCount = filtered.size();

        int completedCycleCount =
            calculateTotalCompletedCycleCount(filtered);

        List<LocalDate> achievedDatesAsc =
            calculateCycleAchievedDates(allRecords);

        int currentStreakDays =
            calculateCurrentStreak(achievedDatesAsc);

        int longestStreakDays =
            calculateLongestStreak(achievedDatesAsc);

        return StatisticsResponse.of(
            resolvedPeriod.name().toLowerCase(),
            totalFocusedSeconds,
            totalFocusedHours,
            currentStreakDays,
            longestStreakDays,
            completedCupCount,
            completedCycleCount
        );
    }

    @Transactional(readOnly = true)
    public CalendarResponse getCalendar(
        String userId,
        int year,
        int month
    ) {
        User user = findUserByUserIdOrThrow(userId);
        validateUserStatus(user.getUserStatus());
        validateYearMonth(year, month);

        YearMonth yearMonth = YearMonth.of(year, month);

        List<FocusRecord> records =
            focusRecordRepository
                .findAllByUserIdAndFocusedDateBetween(
                    user.getId(),
                    yearMonth.atDay(1),
                    yearMonth.atEndOfMonth()
                );

        Map<LocalDate, List<FocusRecord>> byDate =
            records.stream()
                .collect(
                    Collectors.groupingBy(
                        FocusRecord::getFocusedDate
                    )
                );

        Map<LocalDate, Long> secondsByDate =
            byDate.entrySet().stream()
                .collect(
                    Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> sumFocusedSeconds(entry.getValue())
                    )
                );

        Map<LocalDate, Integer> cupCountByDate =
            byDate.entrySet().stream()
                .collect(
                    Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue().size()
                    )
                );

        int maxCupCountInMonth =
            cupCountByDate.values().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElse(0);

        List<CalendarResponse.DayStat> days =
            cupCountByDate.entrySet().stream()
                .map(entry ->
                    new CalendarResponse.DayStat(
                        entry.getKey(),
                        calculateIntensityLevel(
                            entry.getValue(),
                            maxCupCountInMonth
                        )
                    )
                )
                .sorted(
                    Comparator.comparing(
                        CalendarResponse.DayStat::date
                    )
                )
                .toList();

        long totalFocusedSeconds = sumFocusedSeconds(records);
        int completedCupCount = records.size();

        CalendarResponse.BestDay bestDay =
            findBestDay(byDate, secondsByDate);

        return CalendarResponse.of(
            year,
            month,
            totalFocusedSeconds,
            completedCupCount,
            bestDay,
            days
        );
    }

    // =============== 조회 메서드 =============== //

    private User findUserByUserIdOrThrow(String userId) {
        return userRepository.findByUserId(userId)
            .orElseThrow(() ->
                new UserException(UserErrorCode.USER_NOT_FOUND)
            );
    }

    private List<FocusRecord> findFocusRecordByUserIdOrNull(
        Long userId
    ) {
        return focusRecordRepository.findAllByUserId(userId);
    }

    // =============== 검증 메서드 =============== //

    private void validateUserStatus(UserStatus userStatus) {
        switch (userStatus) {
            case WITHDRAWN -> throw new UserException(UserErrorCode.WITHDRAWN_USER);
            case SUSPENDED -> throw new UserException(UserErrorCode.SUSPENDED_USER);
            case ACTIVE -> { }
        }
    }

    private StatisticsPeriod resolvePeriod(
        StatisticsPeriod period
    ) {
        return period == null
            ? StatisticsPeriod.ALL
            : period;
    }

    private void validateYearMonth(int year, int month) {
        if (String.valueOf(year).length() != 4) {
            throw new StatisticsException(StatisticsErrorCode.INVALID_YEAR_MONTH);
        }

        if (month < 1 || month > 12) {
            throw new StatisticsException(StatisticsErrorCode.INVALID_YEAR_MONTH);
        }
    }

    // =============== 계산 메서드 =============== //

    private List<FocusRecord> filterByPeriod(
        List<FocusRecord> records,
        StatisticsPeriod period
    ) {
        LocalDate today = LocalDate.now(ZONE);

        return switch (period) {
            case TODAY -> records.stream()
                .filter(record ->
                    record.getFocusedDate().equals(today)
                )
                .toList();

            case MONTH -> {
                YearMonth currentMonth = YearMonth.from(today);

                yield records.stream()
                    .filter(record ->
                        YearMonth.from(record.getFocusedDate())
                            .equals(currentMonth)
                    )
                    .toList();
            }

            case ALL -> records;
        };
    }

    private long sumFocusedSeconds(
        List<FocusRecord> records
    ) {
        return records.stream()
            .mapToLong(FocusRecord::getFocusedSeconds)
            .sum();
    }

    private int calculateTotalCompletedCycleCount(
        List<FocusRecord> records
    ) {
        Map<LocalDate, List<FocusRecord>> byDate =
            records.stream()
                .collect(
                    Collectors.groupingBy(FocusRecord::getFocusedDate)
                );

        return byDate.values().stream()
            .mapToInt(this::calculateDayCompletedCycleCount)
            .sum();
    }

    private int calculateDayCompletedCycleCount(
        List<FocusRecord> dayRecords
    ) {
        int cycleCount = dayRecords.get(0).getCycleCount();

        if (cycleCount <= 0) {
            return 0;
        }

        return dayRecords.size() / cycleCount;
    }

    private int calculateIntensityLevel(
        int cupCount,
        int maxCupCountInMonth
    ) {
        if (cupCount <= 0 || maxCupCountInMonth <= 0) {
            return 0;
        }

        double ratio = (double) cupCount / maxCupCountInMonth;
        int level = (int) Math.ceil(ratio * MAX_INTENSITY_LEVEL);

        return Math.min(Math.max(level, 1), MAX_INTENSITY_LEVEL);
    }

    private CalendarResponse.BestDay findBestDay(
        Map<LocalDate, List<FocusRecord>> byDate,
        Map<LocalDate, Long> secondsByDate
    ) {
        return secondsByDate.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(entry -> {
                LocalDate date = entry.getKey();

                return new CalendarResponse.BestDay(
                    date,
                    entry.getValue(),
                    byDate.get(date).size()
                );
            })
            .orElse(null);
    }

    private List<LocalDate> calculateCycleAchievedDates(
        List<FocusRecord> records
    ) {
        Map<LocalDate, List<FocusRecord>> byDate =
            records.stream()
                .collect(
                    Collectors.groupingBy(FocusRecord::getFocusedDate)
                );

        return byDate.entrySet().stream()
            .filter(entry ->
                calculateDayCompletedCycleCount(entry.getValue()) >= 1
            )
            .map(Map.Entry::getKey)
            .sorted()
            .toList();
    }

    private int calculateCurrentStreak(
        List<LocalDate> achievedDatesAsc
    ) {
        if (achievedDatesAsc.isEmpty()) {
            return 0;
        }

        LocalDate today = LocalDate.now(ZONE);

        LocalDate mostRecent =
            achievedDatesAsc.get(
                achievedDatesAsc.size() - 1
            );

        if (
            !mostRecent.equals(today)
                && !mostRecent.equals(today.minusDays(1))
        ) {
            return 0;
        }

        Set<LocalDate> dateSet =
            new HashSet<>(achievedDatesAsc);

        int streak = 0;
        LocalDate cursor = mostRecent;

        while (dateSet.contains(cursor)) {
            streak++;
            cursor = cursor.minusDays(1);
        }

        return streak;
    }

    private int calculateLongestStreak(
        List<LocalDate> achievedDatesAsc
    ) {
        if (achievedDatesAsc.isEmpty()) {
            return 0;
        }

        int longest = 1;
        int current = 1;

        for (int i = 1; i < achievedDatesAsc.size(); i++) {
            boolean consecutive =
                ChronoUnit.DAYS.between(
                    achievedDatesAsc.get(i - 1),
                    achievedDatesAsc.get(i)
                ) == 1;

            current = consecutive
                ? current + 1
                : 1;

            longest = Math.max(longest, current);
        }

        return longest;
    }
}