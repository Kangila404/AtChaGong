package org.example.server.timer.presentation.dto.res;

import org.example.server.timer.domain.models.TimerSetting;

public record SaveTimerResponse(
    int focusMinutes,
    int breakMinutes,
    int cycleCount
) {
    public static SaveTimerResponse from(TimerSetting timerSetting) {
        return new SaveTimerResponse(
            timerSetting.getFocusMinutes(),
            timerSetting.getBreakMinutes(),
            timerSetting.getCycleCount()
        );
    }
}
