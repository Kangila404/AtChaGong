package org.example.server.timer.presentation.dto.req;

public record SaveTimerRequest(
    Long beverageId,
    Integer focusMinutes,
    Integer breakMinutes,
    Integer cycleCount
) {
}
