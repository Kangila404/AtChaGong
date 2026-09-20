package org.example.server.attendance.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Optional;
import org.example.server.attendance.domain.models.AttendanceRecord;
import org.example.server.attendance.domain.repository.AttendanceRecordRepository;
import org.example.server.coin.application.CoinService;
import org.example.server.user.domain.enums.UserRole;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.example.server.user.domain.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceTest {
    @InjectMocks private AttendanceService attendanceService;
    @Mock private AttendanceRecordRepository attendanceRepository;
    @Mock private UserRepository userRepository;
    @Mock private CoinService coinService;

    @Test
    void grantsSeventyCoinsOnSeventhConsecutiveDay() {
        User user = user();
        LocalDate today = LocalDate.now(java.time.ZoneId.of("Asia/Seoul"));
        AttendanceRecord previous = AttendanceRecord.create(user, today.minusDays(1), 6, 10);
        given(userRepository.findByUserId("user")).willReturn(Optional.of(user));
        given(attendanceRepository.findByUserIdAndAttendanceDate(1L, today)).willReturn(Optional.empty());
        given(attendanceRepository.findLatestByUserId(1L)).willReturn(Optional.of(previous));
        given(attendanceRepository.save(any())).willAnswer(i -> i.getArgument(0));
        given(coinService.changeBalance(any(), any(Long.class), any(), any(), any())).willReturn(70L);

        attendanceService.attend("user");

        ArgumentCaptor<AttendanceRecord> captor = ArgumentCaptor.forClass(AttendanceRecord.class);
        verify(attendanceRepository).save(captor.capture());
        assertThat(captor.getValue().getConsecutiveDay()).isEqualTo(7);
        assertThat(captor.getValue().getGrantedCoin()).isEqualTo(70);
    }

    @Test
    void resetsToFirstDayAfterSeventhDay() {
        User user = user();
        LocalDate today = LocalDate.now(java.time.ZoneId.of("Asia/Seoul"));
        AttendanceRecord previous = AttendanceRecord.create(user, today.minusDays(1), 7, 70);
        given(userRepository.findByUserId("user")).willReturn(Optional.of(user));
        given(attendanceRepository.findByUserIdAndAttendanceDate(1L, today)).willReturn(Optional.empty());
        given(attendanceRepository.findLatestByUserId(1L)).willReturn(Optional.of(previous));
        given(attendanceRepository.save(any())).willAnswer(i -> i.getArgument(0));
        given(coinService.changeBalance(any(), any(Long.class), any(), any(), any())).willReturn(80L);

        attendanceService.attend("user");

        ArgumentCaptor<AttendanceRecord> captor = ArgumentCaptor.forClass(AttendanceRecord.class);
        verify(attendanceRepository).save(captor.capture());
        assertThat(captor.getValue().getConsecutiveDay()).isEqualTo(1);
        assertThat(captor.getValue().getGrantedCoin()).isEqualTo(10);
    }

    private User user() { return User.builder().id(1L).userId("user").nickname("t").userStatus(UserStatus.ACTIVE).userRole(UserRole.USER).onboardingCompleted(true).build(); }
}
