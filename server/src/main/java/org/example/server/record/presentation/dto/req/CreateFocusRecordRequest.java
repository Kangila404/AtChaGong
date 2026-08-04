package org.example.server.record.presentation.dto.req;

import java.time.OffsetDateTime;

public record CreateFocusRecordRequest(
    Long beverageId,
    Integer focusMinutes,
    Integer focusedSeconds,
    OffsetDateTime startedAt,
    OffsetDateTime completedAt
) {

}
