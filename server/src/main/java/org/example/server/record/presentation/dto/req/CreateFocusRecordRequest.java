package org.example.server.record.presentation.dto.req;

import java.time.OffsetDateTime;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateFocusRecordRequest(
    @NotNull
    Long beverageId,

    @NotNull
    @Min(5)
    @Max(180)
    Integer focusMinutes,

    @NotNull
    @Min(1)
    Integer breakMinutes,

    @NotNull
    @Min(1)
    Integer cycleCount,

    @NotNull
    @Min(1)
    Integer focusedSeconds,

    @NotNull
    OffsetDateTime startedAt,

    @NotNull
    OffsetDateTime completedAt
) {
    public CreateFocusRecordRequest(
        Long beverageId,
        Integer focusMinutes,
        Integer focusedSeconds,
        OffsetDateTime startedAt,
        OffsetDateTime completedAt
    ) {
        this(beverageId, focusMinutes, 5, 1, focusedSeconds, startedAt, completedAt);
    }
}
