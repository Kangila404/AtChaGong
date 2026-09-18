package org.example.server.attendance.infrastructure.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import org.example.server.attendance.domain.models.AttendanceRecord;
import org.example.server.user.domain.enums.UserRole;
import org.example.server.user.domain.enums.UserStatus;
import org.example.server.user.domain.models.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class AttendanceRecordJpaRepositoryTest {

    @Autowired
    private AttendanceRecordJpaRepository attendanceRecordJpaRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("사용자와 출석 날짜로 출석 기록을 조회한다")
    void findAttendanceRecordByUserIdAndAttendanceDate() {
        User user = persistUser("attendance-user");
        LocalDate attendanceDate = LocalDate.of(2026, 9, 18);
        attendanceRecordJpaRepository.saveAndFlush(AttendanceRecord.create(user, attendanceDate, 1, 10));

        AttendanceRecord attendanceRecord = attendanceRecordJpaRepository
            .findByUser_IdAndAttendanceDate(user.getId(), attendanceDate)
            .orElseThrow();

        assertThat(attendanceRecord.getConsecutiveDay()).isEqualTo(1);
        assertThat(attendanceRecord.getGrantedCoin()).isEqualTo(10);
    }

    @Test
    @DisplayName("같은 사용자의 같은 날짜 출석 기록은 중복 저장할 수 없다")
    void saveDuplicateAttendanceRecordThrowsException() {
        User user = persistUser("duplicate-attendance-user");
        LocalDate attendanceDate = LocalDate.of(2026, 9, 18);
        attendanceRecordJpaRepository.saveAndFlush(AttendanceRecord.create(user, attendanceDate, 1, 10));

        assertThatThrownBy(() -> attendanceRecordJpaRepository.saveAndFlush(
            AttendanceRecord.create(user, attendanceDate, 1, 10)
        )).isInstanceOf(DataIntegrityViolationException.class);
    }

    private User persistUser(String userId) {
        User user = User.builder()
            .userId(userId)
            .nickname("tester")
            .userRole(UserRole.USER)
            .userStatus(UserStatus.ACTIVE)
            .onboardingCompleted(true)
            .build();
        entityManager.persist(user);
        entityManager.flush();
        return user;
    }
}
