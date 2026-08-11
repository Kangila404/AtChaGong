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
import org.example.server.statistics.domain.enums.StatisticsPeriod; // 변경: enum import
import org.example.server.statistics.presentation.dto.req.StatisticsRequest;
import org.example.server.statistics.presentation.dto.res.CalendarResponse;
import org.example.server.statistics.presentation.dto.res.StatisticsResponse;
import org.example.server.timer.domain.models.TimerSetting;
import org.example.server.timer.domain.repository.TimerSettingRepository;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private static final ZoneId ZONE = ZoneId.of("Asia/Seoul");

    private final UserRepository userRepository;
    private final FocusRecordRepository focusRecordRepository;
    private final TimerSettingRepository timerSettingRepository;

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

        int cycleCount = resolveCycleCount(user.getId());

        long totalFocusedSeconds = sumFocusedSeconds(filtered);
        long totalFocusedHours = totalFocusedSeconds / 3600;
        int completedCupCount = filtered.size();

        int completedCycleCount =
            calculateTotalCompletedCycleCount(filtered, cycleCount);

        List<LocalDate> achievedDatesAsc =
            calculateCycleAchievedDates(allRecords, cycleCount);

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

        int cycleCount = resolveCycleCount(user.getId());
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

        List<CalendarResponse.DayStat> days =
            byDate.entrySet().stream()
                .map(entry ->
                    toDayStat(
                        entry.getKey(),
                        entry.getValue(),
                        cycleCount
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

        int completedCycleCount =
            calculateTotalCompletedCycleCount(records, cycleCount);

        return new CalendarResponse(
            year,
            month,
            totalFocusedSeconds,
            completedCupCount,
            completedCycleCount,
            days
        );
    }

    // =============== 조회 메서드 =============== //

    private User findUserByUserIdOrThrow(String userId) {
        return userRepository.findByUserId(userId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "유저를 찾을 수 없습니다."
                )
            );
    }

    private User findUserByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() ->
                new IllegalArgumentException(
                    "유저를 찾을 수 없습니다."
                )
            );
    }

    private List<FocusRecord> findFocusRecordByUserIdOrNull(
        Long userId
    ) {
        return focusRecordRepository.findAllByUserId(userId);
    }

    private int resolveCycleCount(Long userId) {
        return timerSettingRepository.findByUserId(userId)
            .map(TimerSetting::getCycleCount)
            .orElse(TimerSetting.DEFAULT_CYCLE_COUNT);
    }

    // =============== 검증 메서드 =============== //

    private void validateUserStatus(UserStatus userStatus) {
        if (!userStatus.equals(UserStatus.ACTIVE)) {
            throw new IllegalArgumentException(
                "비활성화된 유저입니다."
            );
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
            throw new IllegalArgumentException(
                "year는 4자리 정수여야 합니다."
            );
        }

        if (month < 1 || month > 12) {
            throw new IllegalArgumentException(
                "month는 1~12 범위여야 합니다."
            );
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


    private int calculateCompletedCycleCount(
        int completedCupCount,
        int cycleCount
    ) {
        return cycleCount > 0
            ? completedCupCount / cycleCount
            : 0;
    }

    private int calculateTotalCompletedCycleCount(
        List<FocusRecord> records,
        int cycleCount
    ) {
        if (cycleCount <= 0) {
            return 0;
        }

        Map<LocalDate, Long> countByDate =
            records.stream()
                .collect(
                    Collectors.groupingBy(
                        FocusRecord::getFocusedDate,
                        Collectors.counting()
                    )
                );

        return countByDate.values().stream()
            .mapToInt(count ->
                (int) (count / cycleCount)
            )
            .sum();
    }

    private CalendarResponse.DayStat toDayStat(
        LocalDate date,
        List<FocusRecord> dayRecords,
        int cycleCount
    ) {
        long focusedSeconds = sumFocusedSeconds(dayRecords);
        int completedCupCount = dayRecords.size();

        int completedCycleCount =
            calculateCompletedCycleCount(
                completedCupCount,
                cycleCount
            );

        return new CalendarResponse.DayStat(
            date,
            focusedSeconds,
            completedCupCount,
            completedCycleCount,
            completedCycleCount >= 1
        );
    }

    private List<LocalDate> calculateCycleAchievedDates(
        List<FocusRecord> records,
        int cycleCount
    ) {
        if (cycleCount <= 0) {
            return List.of();
        }

        Map<LocalDate, Long> countByDate =
            records.stream()
                .collect(
                    Collectors.groupingBy(
                        FocusRecord::getFocusedDate,
                        Collectors.counting()
                    )
                );

        return countByDate.entrySet().stream()
            .filter(entry ->
                entry.getValue() / cycleCount >= 1
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