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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

        Beverage beverage = findBeverageByIdOrThrow(request.beverageId());
        LocalDateTime startedAt = toSeoulLocalDateTime(request.startedAt());
        LocalDateTime completedAt = toSeoulLocalDateTime(request.completedAt());

        validateDuplicateFocusRecord(user.getId(), startedAt);

        FocusRecord focusRecord = FocusRecord.create(
            user.getId(),
            beverage,
            request.focusMinutes(),
            request.focusedSeconds(),
            startedAt,
            completedAt
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

    private User findUserByUserIdOrThrow(String userId) {
        return userRepository.findByUserId(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저를 찾을 수 없습니다."));
    }

    private Beverage findBeverageByIdOrThrow(Long beverageId) {
        if (beverageId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "음료 ID가 올바르지 않습니다.");
        }
        return beverageRepository.findById(beverageId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "음료를 찾을 수 없습니다."));
    }

    private TimerSetting findTimerSettingByUserIdOrThrow(Long userId) {
        return timerSettingRepository.findByUserId(userId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "타이머 설정을 찾을 수 없습니다."));
    }

    private void validateUserStatus(User user) {
        if (!user.getUserStatus().equals(UserStatus.ACTIVE)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "비활성 유저입니다.");
        }
    }

    private void validateCreateRequest(CreateFocusRecordRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "집중 기록 요청 값이 올바르지 않습니다.");
        }
        validateTimeRange(request);
        validateFocusMinutes(request.focusMinutes());
        validateFocusedSeconds(request.focusedSeconds());
        validateFocusedSecondsRange(request);
    }

    private void validateFocusMinutes(Integer focusMinutes) {
        if (focusMinutes == null || focusMinutes < 5 || focusMinutes > 180 || focusMinutes % 5 != 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "집중 시간이 올바르지 않습니다.");
        }
    }

    private void validateFocusedSeconds(Integer focusedSeconds) {
        if (focusedSeconds == null || focusedSeconds < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "실제 집중 시간이 올바르지 않습니다.");
        }
    }

    private void validateTimeRange(CreateFocusRecordRequest request) {
        if (request.startedAt() == null || request.completedAt() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "집중 시간 범위가 올바르지 않습니다.");
        }
        if (!request.startedAt().isBefore(request.completedAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "집중 시간 범위가 올바르지 않습니다.");
        }
        if (request.completedAt().toInstant().isAfter(Instant.now().plus(Duration.ofMinutes(1)))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "집중 시간 범위가 올바르지 않습니다.");
        }
    }

    private void validateFocusedSecondsRange(CreateFocusRecordRequest request) {
        if (request.focusedSeconds() < request.focusMinutes() * 60) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "완료되지 않은 집중 기록입니다.");
        }

        long elapsedSeconds = Duration.between(request.startedAt(), request.completedAt()).getSeconds();
        if (request.focusedSeconds() > elapsedSeconds + 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "집중 시간 범위가 올바르지 않습니다.");
        }
    }

    private void validateDuplicateFocusRecord(Long userId, LocalDateTime startedAt) {
        if (focusRecordRepository.existsByUserIdAndStartedAt(userId, startedAt)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 저장된 집중 기록입니다.");
        }
    }

    private LocalDate parseDateOrThrow(String date) {
        if (date == null || !date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "날짜가 올바르지 않습니다.");
        }

        try {
            return LocalDate.parse(date, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "날짜가 올바르지 않습니다.");
        }
    }

    private LocalDateTime toSeoulLocalDateTime(OffsetDateTime dateTime) {
        return dateTime.atZoneSameInstant(SEOUL_ZONE).toLocalDateTime();
    }

    private OffsetDateTime toSeoulOffsetDateTime(LocalDateTime dateTime) {
        return dateTime.atZone(SEOUL_ZONE).toOffsetDateTime();
    }

    private int sumFocusedSeconds(List<FocusRecord> focusRecords) {
        return focusRecords.stream()
            .mapToInt(FocusRecord::getFocusedSeconds)
            .sum();
    }

    private int calculateCompletedCycleCount(int completedCupCount, int cycleCount) {
        if (cycleCount < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "타이머 설정이 올바르지 않습니다.");
        }
        return completedCupCount / cycleCount;
    }
}
