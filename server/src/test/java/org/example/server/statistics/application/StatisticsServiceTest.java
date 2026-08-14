package org.example.server.statistics.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.example.server.beverage.domain.models.Beverage;
import org.example.server.record.domain.models.FocusRecord;
import org.example.server.record.domain.repository.FocusRecordRepository;
import org.example.server.statistics.domain.enums.StatisticsPeriod;
import org.example.server.statistics.exception.StatisticsErrorCode;
import org.example.server.statistics.exception.StatisticsException;
import org.example.server.statistics.presentation.dto.req.StatisticsRequest;
import org.example.server.statistics.presentation.dto.res.CalendarResponse;
import org.example.server.statistics.presentation.dto.res.StatisticsResponse;
import org.example.server.user.domain.enums.UserRole;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.example.server.user.exception.UserErrorCode;
import org.example.server.user.exception.UserException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");
    private static final String USER_ID = "user-1";
    private static final long USER_PK = 1L;

    @InjectMocks
    private StatisticsService statisticsService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FocusRecordRepository focusRecordRepository;

    @Test
    @DisplayName("전체 통계는 총 집중 시간, 컵 수, 완료 사이클, 연속 달성일을 계산한다")
    void getStatisticsForAllCalculatesSummary() {
        LocalDate today = LocalDate.now(SEOUL_ZONE);
        List<FocusRecord> records = List.of(
            focusRecord(today.minusDays(2), 3_600),
            focusRecord(today.minusDays(1), 1_800),
            focusRecord(today.minusDays(1), 1_800),
            focusRecord(today, 1_500)
        );
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(user(UserStatus.ACTIVE)));
        given(focusRecordRepository.findAllByUserId(USER_PK)).willReturn(records);

        StatisticsResponse response = statisticsService.getStatistics(USER_ID, new StatisticsRequest(StatisticsPeriod.ALL));

        assertThat(response.period()).isEqualTo("all");
        assertThat(response.totalFocusedSeconds()).isEqualTo(8_700);
        assertThat(response.totalFocusedHours()).isEqualTo(2);
        assertThat(response.completedCupCount()).isEqualTo(5);
        assertThat(response.completedCycleCount()).isEqualTo(4);
        assertThat(response.currentStreakDays()).isEqualTo(3);
        assertThat(response.longestStreakDays()).isEqualTo(3);
    }

    @Test
    @DisplayName("오늘 통계는 오늘 기록만 집계한다")
    void getStatisticsForTodayFiltersTodayRecords() {
        LocalDate today = LocalDate.now(SEOUL_ZONE);
        List<FocusRecord> records = List.of(
            focusRecord(today, 1_500),
            focusRecord(today.minusDays(1), 3_600)
        );
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(user(UserStatus.ACTIVE)));
        given(focusRecordRepository.findAllByUserId(USER_PK)).willReturn(records);

        StatisticsResponse response = statisticsService.getStatistics(USER_ID, new StatisticsRequest(StatisticsPeriod.TODAY));

        assertThat(response.period()).isEqualTo("today");
        assertThat(response.totalFocusedSeconds()).isEqualTo(1_500);
        assertThat(response.completedCupCount()).isEqualTo(1);
        assertThat(response.completedCycleCount()).isEqualTo(1);
    }

    @Test
    @DisplayName("탈퇴 유저는 통계를 조회할 수 없다")
    void getStatisticsWithWithdrawnUserThrowsException() {
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(user(UserStatus.WITHDRAWN)));

        assertThatThrownBy(() -> statisticsService.getStatistics(USER_ID, new StatisticsRequest(StatisticsPeriod.ALL)))
            .isInstanceOf(UserException.class)
            .extracting("code")
            .isEqualTo(UserErrorCode.WITHDRAWN_USER.name());
        verify(focusRecordRepository, never()).findAllByUserId(any());
    }

    @Test
    @DisplayName("캘린더는 날짜별 강도와 최고 집중일을 계산한다")
    void getCalendarCalculatesDailyIntensityAndBestDay() {
        int year = 2024;
        int month = 1;
        List<FocusRecord> records = List.of(
            focusRecord(LocalDate.of(year, month, 1), 1_500),
            focusRecord(LocalDate.of(year, month, 2), 2_000),
            focusRecord(LocalDate.of(year, month, 2), 2_500),
            focusRecord(LocalDate.of(year, month, 3), 1_000),
            focusRecord(LocalDate.of(year, month, 3), 1_000),
            focusRecord(LocalDate.of(year, month, 3), 1_000)
        );
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(user(UserStatus.ACTIVE)));
        given(focusRecordRepository.findAllByUserIdAndFocusedDateBetween(
            USER_PK,
            LocalDate.of(year, month, 1),
            LocalDate.of(year, month, 31)
        )).willReturn(records);

        CalendarResponse response = statisticsService.getCalendar(USER_ID, year, month);

        assertThat(response.totalFocusedSeconds()).isEqualTo(9_000);
        assertThat(response.completedCupCount()).isEqualTo(3);
        assertThat(response.bestDay().date()).isEqualTo(LocalDate.of(year, month, 2));
        assertThat(response.bestDay().focusedSeconds()).isEqualTo(4_500);
        assertThat(response.days())
            .extracting(CalendarResponse.DayStat::intensityLevel)
            .containsExactly(2, 4, 0);
    }

    @Test
    @DisplayName("올바르지 않은 월이면 캘린더를 조회하지 않는다")
    void getCalendarWithInvalidMonthThrowsException() {
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(user(UserStatus.ACTIVE)));

        assertThatThrownBy(() -> statisticsService.getCalendar(USER_ID, 2024, 13))
            .isInstanceOf(StatisticsException.class)
            .extracting("code")
            .isEqualTo(StatisticsErrorCode.INVALID_YEAR_MONTH.name());
        verify(focusRecordRepository, never()).findAllByUserIdAndFocusedDateBetween(any(), any(), any());
    }

    private User user(UserStatus status) {
        return User.builder()
            .id(USER_PK)
            .userId(USER_ID)
            .nickname("tester")
            .userStatus(status)
            .userRole(UserRole.USER)
            .onboardingCompleted(true)
            .build();
    }

    private FocusRecord focusRecord(LocalDate focusedDate, int focusedSeconds) {
        return FocusRecord.create(
            USER_PK,
            org.mockito.Mockito.mock(Beverage.class),
            25,
            focusedSeconds,
            LocalDateTime.of(focusedDate, java.time.LocalTime.of(9, 0)),
            LocalDateTime.of(focusedDate, java.time.LocalTime.of(9, 30))
        );
    }
}
