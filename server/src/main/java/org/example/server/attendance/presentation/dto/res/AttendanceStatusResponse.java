package org.example.server.attendance.presentation.dto.res;

public record AttendanceStatusResponse(
    boolean attendedToday,
    int consecutiveDay
) {
}
