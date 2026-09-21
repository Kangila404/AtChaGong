package org.example.server.attendance.presentation.dto.res;

import java.time.LocalDate;

public record AttendanceResponse(
    LocalDate attendanceDate,
    int consecutiveDay,
    long grantedCoin,
    long balance
) {
}
