package org.example.server.record.presentation.dto.req;

import java.time.OffsetDateTime;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateFocusRecordRequest(
    @NotNull
    Long beverageId,

    @NotNull
    @Min(25)
    @Max(60)
    Integer focusMinutes,

    @NotNull
    @Min(1)
    Integer focusedSeconds,

    @NotNull
    OffsetDateTime startedAt,

    @NotNull
    OffsetDateTime completedAt
) {
}
