package org.example.server.attendance.application;

import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.example.server.attendance.domain.models.AttendanceRecord;
import org.example.server.attendance.domain.repository.AttendanceRecordRepository;
import org.example.server.attendance.exception.AttendanceErrorCode;
import org.example.server.attendance.exception.AttendanceException;
import org.example.server.attendance.presentation.dto.res.AttendanceResponse;
import org.example.server.coin.application.CoinService;
import org.example.server.coin.domain.enums.CoinReferenceType;
import org.example.server.coin.domain.enums.CoinTransactionType;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.example.server.user.exception.UserErrorCode;
import org.example.server.user.exception.UserException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttendanceService {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");
    public static final long DAILY_ATTENDANCE_REWARD = 10L;

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final UserRepository userRepository;
    private final CoinService coinService;

    @Transactional
    public AttendanceResponse attend(String userId) {
        User user = findActiveUser(userId);
        LocalDate today = LocalDate.now(SEOUL_ZONE);
        if (attendanceRecordRepository.findByUserIdAndAttendanceDate(user.getId(), today).isPresent()) {
            throw new AttendanceException(AttendanceErrorCode.ALREADY_ATTENDED);
        }

        int consecutiveDay = attendanceRecordRepository.findLatestByUserId(user.getId())
            .filter(record -> record.getAttendanceDate().plusDays(1).equals(today))
            .map(record -> record.getConsecutiveDay() + 1)
            .orElse(1);
        AttendanceRecord record = attendanceRecordRepository.save(
            AttendanceRecord.create(user, today, consecutiveDay, DAILY_ATTENDANCE_REWARD)
        );
        long balance = coinService.changeBalance(
            user,
            DAILY_ATTENDANCE_REWARD,
            CoinTransactionType.ATTENDANCE,
            CoinReferenceType.ATTENDANCE,
            record.getId()
        );
        return new AttendanceResponse(today, consecutiveDay, DAILY_ATTENDANCE_REWARD, balance);
    }

    private User findActiveUser(String userId) {
        User user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
        if (user.getUserStatus() == UserStatus.SUSPENDED) {
            throw new UserException(UserErrorCode.SUSPENDED_USER);
        }
        if (user.getUserStatus() == UserStatus.WITHDRAWN) {
            throw new UserException(UserErrorCode.WITHDRAWN_USER);
        }
        return user;
    }
}
