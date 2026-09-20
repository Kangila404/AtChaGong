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
import org.example.server.beverage.domain.repository.UserBeverageRepository;
import org.example.server.coin.application.CoinService;
import org.example.server.coin.domain.enums.CoinReferenceType;
import org.example.server.coin.domain.enums.CoinTransactionType;
import org.example.server.record.domain.models.FocusRecord;
import org.example.server.record.domain.repository.FocusRecordRepository;
import org.example.server.record.exception.RecordErrorCode;
import org.example.server.record.exception.RecordException;
import org.example.server.record.presentation.dto.req.CreateFocusRecordRequest;
import org.example.server.record.presentation.dto.res.DailyFocusRecordResponse;
import org.example.server.record.presentation.dto.res.FocusRecordResponse;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.example.server.user.exception.UserErrorCode;
import org.example.server.user.exception.UserException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FocusRecordService {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    public static final long FOCUS_COMPLETION_REWARD = 10L;

    private final FocusRecordRepository focusRecordRepository;
    private final BeverageRepository beverageRepository;
    private final UserBeverageRepository userBeverageRepository;
    private final UserRepository userRepository;
    private final CoinService coinService;

    @Transactional
    public FocusRecordResponse createFocusRecord(String userId, CreateFocusRecordRequest request) {
        User user = findUserByUserIdOrThrow(userId);
        validateUserStatus(user.getUserStatus());
        validateCreateRequest(request);

        Beverage beverage = findOwnedBeverage(user.getId(), request.beverageId());
        FocusRecord savedFocusRecord = saveFocusRecord(user, beverage, request);

        return toFocusRecordResponse(savedFocusRecord);
    }

    private Beverage findOwnedBeverage(Long userId, Long beverageId) {
        Beverage beverage = findBeverageByIdOrThrow(beverageId);
        validateBeverageOwnership(userId, beverage.getId());
        return beverage;
    }

    private FocusRecord saveFocusRecord(User user, Beverage beverage, CreateFocusRecordRequest request) {
        LocalDateTime startedAt = toSeoulLocalDateTime(request.startedAt());
        LocalDateTime completedAt = toSeoulLocalDateTime(request.completedAt());
        validateDuplicateFocusRecord(user.getId(), startedAt);
        FocusRecord focusRecord = FocusRecord.create(
            user.getId(),
            beverage,
            request.focusMinutes(),
            request.breakMinutes(),
            request.cycleCount(),
            request.focusedSeconds(),
            startedAt,
            completedAt
        );
        FocusRecord savedFocusRecord = focusRecordRepository.save(focusRecord);
        coinService.changeBalance(
            user,
            FOCUS_COMPLETION_REWARD,
            CoinTransactionType.FOCUS_COMPLETION,
            CoinReferenceType.FOCUS_RECORD,
            savedFocusRecord.getId()
        );
        return savedFocusRecord;
    }

    private FocusRecordResponse toFocusRecordResponse(FocusRecord focusRecord) {
        return FocusRecordResponse.of(focusRecord, toSeoulOffsetDateTime(focusRecord.getStartedAt()),
            toSeoulOffsetDateTime(focusRecord.getCompletedAt()));
    }

    @Transactional(readOnly = true)
    public DailyFocusRecordResponse getDailyFocusRecords(String userId, String date) {
        User user = findUserByUserIdOrThrow(userId);
        validateUserStatus(user.getUserStatus());

        LocalDate focusedDate = parseDateOrThrow(date);
        List<FocusRecord> focusRecords = focusRecordRepository.findByUserIdAndFocusedDate(user.getId(), focusedDate);

        int totalFocusedSeconds = sumFocusedSeconds(focusRecords);
        int completedCupCount = sumCupCount(focusRecords);

        return DailyFocusRecordResponse.of(focusedDate, totalFocusedSeconds, completedCupCount);
    }

    private User findUserByUserIdOrThrow(String userId) {
        return userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    private Beverage findBeverageByIdOrThrow(Long beverageId) {
        if (beverageId == null) {
            throw new RecordException(RecordErrorCode.INVALID_REQUEST);
        }
        return beverageRepository.findById(beverageId)
            .orElseThrow(() -> new RecordException(RecordErrorCode.BEVERAGE_NOT_FOUND));
    }

    private void validateBeverageOwnership(Long userId, Long beverageId) {
        userBeverageRepository.findByUserIdAndBeverageId(userId, beverageId)
            .orElseThrow(() -> new RecordException(RecordErrorCode.BEVERAGE_NOT_OWNED));
    }

    private void validateUserStatus(UserStatus userStatus){
        switch (userStatus) {
            case WITHDRAWN -> throw new UserException(UserErrorCode.WITHDRAWN_USER);
            case SUSPENDED -> throw new UserException(UserErrorCode.SUSPENDED_USER);
            case ACTIVE -> { }
        }
    }

    private void validateCreateRequest(CreateFocusRecordRequest request) {
        if (request == null) {
            throw new RecordException(RecordErrorCode.INVALID_REQUEST);
        }
        validateTimeRange(request);
        validateFocusMinutes(request.focusMinutes());
        validateBreakMinutes(request.breakMinutes());
        validateCycleCount(request.cycleCount());
        validateFocusedSeconds(request.focusedSeconds());
        validateFocusedSecondsRange(request);
    }

    private void validateFocusMinutes(Integer focusMinutes) {
        if (focusMinutes == null || focusMinutes < 25 || focusMinutes > 60 || focusMinutes % 5 != 0) {
            throw new RecordException(RecordErrorCode.INVALID_FOCUS_MINUTES);
        }
    }

    private void validateFocusedSeconds(Integer focusedSeconds) {
        if (focusedSeconds == null || focusedSeconds < 1) {
            throw new RecordException(RecordErrorCode.INVALID_FOCUSED_SECONDS);
        }
    }

    private void validateBreakMinutes(Integer breakMinutes) {
        if (breakMinutes == null || breakMinutes < 1) {
            throw new RecordException(RecordErrorCode.INVALID_REQUEST);
        }
    }

    private void validateCycleCount(Integer cycleCount) {
        if (cycleCount == null || cycleCount < 1) {
            throw new RecordException(RecordErrorCode.INVALID_REQUEST);
        }
    }

    private void validateTimeRange(CreateFocusRecordRequest request) {
        if (request.startedAt() == null || request.completedAt() == null) {
            throw new RecordException(RecordErrorCode.INVALID_TIME_RANGE);
        }
        if (!request.startedAt().isBefore(request.completedAt())) {
            throw new RecordException(RecordErrorCode.INVALID_TIME_RANGE);
        }
        if (request.completedAt().toInstant().isAfter(Instant.now().plus(Duration.ofMinutes(1)))) {
            throw new RecordException(RecordErrorCode.INVALID_TIME_RANGE);
        }
    }

    private void validateFocusedSecondsRange(CreateFocusRecordRequest request) {
        if (request.focusedSeconds() < request.focusMinutes() * 60) {
            throw new RecordException(RecordErrorCode.INCOMPLETE_FOCUS);
        }

        long elapsedSeconds = Duration.between(request.startedAt(), request.completedAt()).getSeconds();
        if (request.focusedSeconds() > elapsedSeconds + 5) {
            throw new RecordException(RecordErrorCode.INVALID_TIME_RANGE);
        }
    }

    private void validateDuplicateFocusRecord(Long userId, LocalDateTime startedAt) {
        if (focusRecordRepository.existsByUserIdAndStartedAt(userId, startedAt)) {
            throw new RecordException(RecordErrorCode.DUPLICATE_FOCUS_RECORD);
        }
    }

    private LocalDate parseDateOrThrow(String date) {
        if (date == null || !date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new RecordException(RecordErrorCode.INVALID_DATE);
        }

        try {
            return LocalDate.parse(date, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new RecordException(RecordErrorCode.INVALID_DATE);
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

    // 컵 개수 = 레코드별 (focusedSeconds ÷ (focusMinutes × 60)) 합계.
    // cycleCount 컬럼 없이, 저장된 실공부시간과 회당 집중시간으로 반복 횟수를 역산한다.
    private int sumCupCount(List<FocusRecord> focusRecords) {
        return focusRecords.stream()
            .mapToInt(record -> record.getFocusedSeconds() / (record.getFocusMinutes() * 60))
            .sum();
    }
}
