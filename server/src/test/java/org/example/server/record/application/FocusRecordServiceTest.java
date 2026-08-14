package org.example.server.record.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.example.server.beverage.domain.models.Beverage;
import org.example.server.beverage.domain.repository.BeverageRepository;
import org.example.server.record.domain.models.FocusRecord;
import org.example.server.record.domain.repository.FocusRecordRepository;
import org.example.server.record.exception.RecordErrorCode;
import org.example.server.record.exception.RecordException;
import org.example.server.record.presentation.dto.req.CreateFocusRecordRequest;
import org.example.server.record.presentation.dto.res.DailyFocusRecordResponse;
import org.example.server.record.presentation.dto.res.FocusRecordResponse;
import org.example.server.user.domain.enums.UserRole;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FocusRecordServiceTest {

    private static final String USER_ID = "user-1";
    private static final long USER_PK = 1L;
    private static final long BEVERAGE_ID = 10L;

    @InjectMocks
    private FocusRecordService focusRecordService;

    @Mock
    private FocusRecordRepository focusRecordRepository;

    @Mock
    private BeverageRepository beverageRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("유효한 요청이면 집중 기록을 저장한다")
    void createFocusRecordSavesRecord() {
        Beverage beverage = beverage();
        OffsetDateTime startedAt = OffsetDateTime.of(2024, 1, 15, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime completedAt = OffsetDateTime.of(2024, 1, 15, 0, 30, 0, 0, ZoneOffset.UTC);
        CreateFocusRecordRequest request = new CreateFocusRecordRequest(BEVERAGE_ID, 25, 1_500, startedAt, completedAt);
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(activeUser()));
        given(beverageRepository.findById(BEVERAGE_ID)).willReturn(Optional.of(beverage));
        given(focusRecordRepository.existsByUserIdAndStartedAt(USER_PK, LocalDateTime.of(2024, 1, 15, 9, 0)))
            .willReturn(false);
        given(focusRecordRepository.save(any(FocusRecord.class))).willAnswer(invocation -> invocation.getArgument(0));

        FocusRecordResponse response = focusRecordService.createFocusRecord(USER_ID, request);

        ArgumentCaptor<FocusRecord> captor = ArgumentCaptor.forClass(FocusRecord.class);
        verify(focusRecordRepository).save(captor.capture());
        FocusRecord saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(USER_PK);
        assertThat(saved.getBeverage()).isSameAs(beverage);
        assertThat(saved.getFocusMinutes()).isEqualTo(25);
        assertThat(saved.getFocusedSeconds()).isEqualTo(1_500);
        assertThat(saved.getStartedAt()).isEqualTo(LocalDateTime.of(2024, 1, 15, 9, 0));
        assertThat(saved.getCompletedAt()).isEqualTo(LocalDateTime.of(2024, 1, 15, 9, 30));
        assertThat(saved.getFocusedDate()).isEqualTo(LocalDate.of(2024, 1, 15));
        assertThat(response.focusMinutes()).isEqualTo(25);
    }

    @Test
    @DisplayName("집중 시간이 완료 기준보다 짧으면 저장하지 않는다")
    void createFocusRecordWithIncompleteFocusThrowsException() {
        OffsetDateTime startedAt = OffsetDateTime.of(2024, 1, 15, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime completedAt = OffsetDateTime.of(2024, 1, 15, 0, 30, 0, 0, ZoneOffset.UTC);
        CreateFocusRecordRequest request = new CreateFocusRecordRequest(BEVERAGE_ID, 25, 1_499, startedAt, completedAt);
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(activeUser()));

        assertThatThrownBy(() -> focusRecordService.createFocusRecord(USER_ID, request))
            .isInstanceOf(RecordException.class)
            .extracting("code")
            .isEqualTo(RecordErrorCode.INCOMPLETE_FOCUS.name());
        verify(focusRecordRepository, never()).save(any());
    }

    @Test
    @DisplayName("일별 집중 기록은 총 집중 시간과 완료 사이클 수를 계산한다")
    void getDailyFocusRecordsCalculatesSummary() {
        Beverage beverage = beverage();
        LocalDate focusedDate = LocalDate.of(2024, 1, 15);
        List<FocusRecord> records = List.of(
            focusRecord(beverage, 25, 1_500, LocalDateTime.of(2024, 1, 15, 9, 0)),
            focusRecord(beverage, 25, 1_600, LocalDateTime.of(2024, 1, 15, 10, 0)),
            focusRecord(beverage, 25, 1_700, LocalDateTime.of(2024, 1, 15, 11, 0))
        );
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(activeUser()));
        given(focusRecordRepository.findByUserIdAndFocusedDate(USER_PK, focusedDate)).willReturn(records);

        DailyFocusRecordResponse response = focusRecordService.getDailyFocusRecords(USER_ID, "2024-01-15");

        assertThat(response.date()).isEqualTo(focusedDate);
        assertThat(response.totalFocusedSeconds()).isEqualTo(4_800);
        assertThat(response.completedCupCount()).isEqualTo(3);
    }

    @Test
    @DisplayName("날짜 형식이 올바르지 않으면 일별 기록을 조회하지 않는다")
    void getDailyFocusRecordsWithInvalidDateThrowsException() {
        given(userRepository.findByUserId(USER_ID)).willReturn(Optional.of(activeUser()));

        assertThatThrownBy(() -> focusRecordService.getDailyFocusRecords(USER_ID, "2024-1-15"))
            .isInstanceOf(RecordException.class)
            .extracting("code")
            .isEqualTo(RecordErrorCode.INVALID_DATE.name());
        verify(focusRecordRepository, never()).findByUserIdAndFocusedDate(any(), any());
    }

    private User activeUser() {
        return User.builder()
            .id(USER_PK)
            .userId(USER_ID)
            .nickname("tester")
            .userStatus(UserStatus.ACTIVE)
            .userRole(UserRole.USER)
            .onboardingCompleted(true)
            .build();
    }

    private Beverage beverage() {
        return org.mockito.Mockito.mock(Beverage.class);
    }

    private FocusRecord focusRecord(Beverage beverage, int focusMinutes, int focusedSeconds, LocalDateTime startedAt) {
        return FocusRecord.create(
            USER_PK,
            beverage,
            focusMinutes,
            focusedSeconds,
            startedAt,
            startedAt.plusMinutes(30)
        );
    }
}
