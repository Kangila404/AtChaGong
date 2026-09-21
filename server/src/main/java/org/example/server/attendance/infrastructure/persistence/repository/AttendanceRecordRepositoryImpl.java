package org.example.server.attendance.infrastructure.persistence.repository;

import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.server.attendance.domain.models.AttendanceRecord;
import org.example.server.attendance.domain.repository.AttendanceRecordRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class AttendanceRecordRepositoryImpl implements AttendanceRecordRepository {

    private final AttendanceRecordJpaRepository attendanceRecordJpaRepository;

    @Override
    public Optional<AttendanceRecord> findByUserIdAndAttendanceDate(Long userId, LocalDate attendanceDate) {
        return attendanceRecordJpaRepository.findByUser_IdAndAttendanceDate(userId, attendanceDate);
    }

    @Override
    public AttendanceRecord save(AttendanceRecord attendanceRecord) {
        return attendanceRecordJpaRepository.save(attendanceRecord);
    }
}
