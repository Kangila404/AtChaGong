package org.example.server.attendance.infrastructure.persistence.repository;

import java.time.LocalDate;
import java.util.Optional;
import org.example.server.attendance.domain.models.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRecordJpaRepository extends JpaRepository<AttendanceRecord, Long> {

    Optional<AttendanceRecord> findByUser_IdAndAttendanceDate(Long userId, LocalDate attendanceDate);

    Optional<AttendanceRecord> findFirstByUser_IdOrderByAttendanceDateDesc(Long userId);
    void deleteByUser_Id(Long userId);
}
