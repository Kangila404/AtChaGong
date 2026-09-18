package org.example.server.attendance.domain.repository;

import java.time.LocalDate;
import java.util.Optional;
import org.example.server.attendance.domain.models.AttendanceRecord;

public interface AttendanceRecordRepository {

    Optional<AttendanceRecord> findByUserIdAndAttendanceDate(Long userId, LocalDate attendanceDate);

    AttendanceRecord save(AttendanceRecord attendanceRecord);
}
