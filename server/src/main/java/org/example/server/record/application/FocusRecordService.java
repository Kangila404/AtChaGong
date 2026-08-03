package org.example.server.record.application;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.server.beverage.domain.models.Beverage;
import org.example.server.beverage.domain.repository.BeverageRepository;
import org.example.server.record.domain.models.FocusRecord;
import org.example.server.record.domain.repository.FocusRecordRepository;
import org.example.server.record.presentation.dto.req.CreateFocusRecordRequest;
import org.example.server.record.presentation.dto.res.DailyFocusRecordResponse;
import org.example.server.record.presentation.dto.res.DailyFocusRecordResponse.DailyRecord;
import org.example.server.record.presentation.dto.res.FocusRecordResponse;
import org.example.server.timer.domain.models.TimerSetting;
import org.example.server.timer.domain.repository.TimerSettingRepository;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FocusRecordService {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    private final FocusRecordRepository focusRecordRepository;
    private final BeverageRepository beverageRepository;
    private final TimerSettingRepository timerSettingRepository;
    private final UserRepository userRepository;

    @Transactional
    public FocusRecordResponse createFocusRecord(String userId, CreateFocusRecordRequest request) {
        User user = findUserByUserIdOrThrow(userId);
        validateUserStatus(user);
        validateCreateRequest(request);

        Beverage beverage = findBeverageOrNull(request.beverageId());
        LocalDateTime startedAt = toSeoulLocalDateTime(request.startedAt());
        LocalDateTime completedAt = toSeoulLocalDateTime(request.completedAt());
        LocalDate focusedDate = request.completedAt().atZoneSameInstant(SEOUL_ZONE).toLocalDate();

        validateDuplicateFocusRecord(user.getId(), startedAt);

        FocusRecord focusRecord = FocusRecord.create(
            user.getId(),
            beverage,
            request.focusMinutes(),
            request.focusedSeconds(),
            startedAt,
            completedAt,
            focusedDate
        );
        FocusRecord savedFocusRecord = focusRecordRepository.save(focusRecord);

        return FocusRecordResponse.of(
            savedFocusRecord,
            toSeoulOffsetDateTime(savedFocusRecord.getStartedAt()),
            toSeoulOffsetDateTime(savedFocusRecord.getCompletedAt())
        );
    }

    @Transactional(readOnly = true)
    public DailyFocusRecordResponse getDailyFocusRecords(String userId, String date) {
        User user = findUserByUserIdOrThrow(userId);
        validateUserStatus(user);

        LocalDate focusedDate = parseDateOrThrow(date);
        int cycleCount = findTimerSettingByUserIdOrThrow(user.getId()).getCycleCount();
        List<FocusRecord> focusRecords = focusRecordRepository.findByUserIdAndFocusedDate(user.getId(), focusedDate);
        List<DailyRecord> records = focusRecords.stream()
            .map(focusRecord -> DailyRecord.of(
                focusRecord,
                toSeoulOffsetDateTime(focusRecord.getStartedAt()),
                toSeoulOffsetDateTime(focusRecord.getCompletedAt())
            ))
            .toList();

        int totalFocusedSeconds = sumFocusedSeconds(focusRecords);
        int completedCycleCount = calculateCompletedCycleCount(records.size(), cycleCount);

        return DailyFocusRecordResponse.of(focusedDate, records, totalFocusedSeconds, completedCycleCount);
    }

    // ============= 조회 메서드 모음 ============= //

    // 1. (String) userId -> User 조회
    private User findUserByUserIdOrThrow(String userId) {
        return userRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
    }

    // 2. (Long) beverageId -> Beverage 조회
    private Beverage findBeverageOrNull(Long beverageId) {
        if (beverageId == null) {
            return null;
        }
        return beverageRepository.findById(beverageId)
            .orElseThrow(() -> new IllegalArgumentException("음료를 찾을 수 없습니다."));
    }

    // 3. (Long) userId -> TimerSetting 조회
    private TimerSetting findTimerSettingByUserIdOrThrow(Long userId) {
        return timerSettingRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalArgumentException("타이머 설정을 찾을 수 없습니다."));
    }

    // ============= 검증 메서드 모음 ============= //

    // 1. 유저 상태 검증
    private void validateUserStatus(User user) {
        if (!user.getUserStatus().equals(UserStatus.ACTIVE)) {
            throw new IllegalArgumentException("비활성 유저입니다.");
        }
    }

    // 2. 집중 기록 생성 요청 검증
    private void validateCreateRequest(CreateFocusRecordRequest request) {
        validateTimeRange(request);
        validateFocusMinutes(request.focusMinutes());
        validateFocusedSeconds(request.focusedSeconds());
        validateFocusedSecondsRange(request);
    }

    // 3. 집중 시간 검증
    private void validateFocusMinutes(Integer focusMinutes) {
        if (focusMinutes == null || focusMinutes < 5 || focusMinutes > 180 || focusMinutes % 5 != 0) {
            throw new IllegalArgumentException("집중 시간이 올바르지 않습니다.");
        }
    }

    // 4. 실제 집중 시간 검증
    private void validateFocusedSeconds(Integer focusedSeconds) {
        if (focusedSeconds == null || focusedSeconds < 1) {
            throw new IllegalArgumentException("실제 집중 시간이 올바르지 않습니다.");
        }
    }

    // 5. 시작/종료 시간 검증
    private void validateTimeRange(CreateFocusRecordRequest request) {
        if (request.startedAt() == null || request.completedAt() == null) {
            throw new IllegalArgumentException("집중 시간 범위가 올바르지 않습니다.");
        }
        if (!request.startedAt().isBefore(request.completedAt())) {
            throw new IllegalArgumentException("집중 시간 범위가 올바르지 않습니다.");
        }
        if (request.completedAt().toInstant().isAfter(Instant.now().plus(Duration.ofMinutes(1)))) {
            throw new IllegalArgumentException("집중 시간 범위가 올바르지 않습니다.");
        }
    }

    // 6. 실제 집중 시간 범위 검증
    private void validateFocusedSecondsRange(CreateFocusRecordRequest request) {
        if (request.focusedSeconds() < request.focusMinutes() * 60) {
            throw new IllegalArgumentException("완료되지 않은 집중 기록입니다.");
        }

        long elapsedSeconds = Duration.between(request.startedAt(), request.completedAt()).getSeconds();
        if (request.focusedSeconds() > elapsedSeconds + 5) {
            throw new IllegalArgumentException("집중 시간 범위가 올바르지 않습니다.");
        }
    }

    // 7. 중복 집중 기록 검증
    private void validateDuplicateFocusRecord(Long userId, LocalDateTime startedAt) {
        if (focusRecordRepository.existsByUserIdAndStartedAt(userId, startedAt)) {
            throw new IllegalArgumentException("이미 저장된 집중 기록입니다.");
        }
    }

    // ============= 날짜 메서드 모음 ============= //

    // 1. 문자열 -> LocalDate 변환
    private LocalDate parseDateOrThrow(String date) {
        if (date == null || !date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new IllegalArgumentException("날짜가 올바르지 않습니다.");
        }

        try {
            return LocalDate.parse(date, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("날짜가 올바르지 않습니다.");
        }
    }

    // 2. OffsetDateTime -> Asia/Seoul LocalDateTime 변환
    private LocalDateTime toSeoulLocalDateTime(OffsetDateTime dateTime) {
        return dateTime.atZoneSameInstant(SEOUL_ZONE).toLocalDateTime();
    }

    // 3. LocalDateTime -> Asia/Seoul OffsetDateTime 변환
    private OffsetDateTime toSeoulOffsetDateTime(LocalDateTime dateTime) {
        return dateTime.atZone(SEOUL_ZONE).toOffsetDateTime();
    }

    // ============= 통계 메서드 모음 ============= //

    // 1. 집중 시간 합계 계산
    private int sumFocusedSeconds(List<FocusRecord> focusRecords) {
        return focusRecords.stream()
            .mapToInt(FocusRecord::getFocusedSeconds)
            .sum();
    }

    // 2. 완료 사이클 수 계산
    private int calculateCompletedCycleCount(int completedCupCount, int cycleCount) {
        if (cycleCount < 1) {
            throw new IllegalArgumentException("타이머 설정이 올바르지 않습니다.");
        }
        return completedCupCount / cycleCount;
    }
}
