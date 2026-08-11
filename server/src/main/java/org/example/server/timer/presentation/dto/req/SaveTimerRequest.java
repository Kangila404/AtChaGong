package org.example.server.timer.presentation.dto.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SaveTimerRequest(
    @NotNull(message = "음료 ID는 필수입니다.")
    @Positive(message = "음료 ID는 양수여야 합니다.")
    Long beverageId,

    @NotNull(message = "집중 시간은 필수입니다.")
    @Min(value = 1, message = "집중 시간은 1분 이상이어야 합니다.")
    Integer focusMinutes,

    @NotNull(message = "휴식 시간은 필수입니다.")
    @Min(value = 1, message = "휴식 시간은 1분 이상이어야 합니다.")
    Integer breakMinutes,

    @NotNull(message = "반복 횟수는 필수입니다.")
    @Min(value = 1, message = "반복 횟수는 1회 이상이어야 합니다.")
    Integer cycleCount
) {
}
