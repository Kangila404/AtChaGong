package org.example.server.timer.presentation.dto.res;

import org.example.server.beverage.presentation.dto.res.BeverageResponse;
import org.example.server.timer.domain.models.TimerSetting;

public record TimerSettingResponse(
    BeverageResponse beverage,
    int focusMinutes,
    int breakMinutes,
    int cycleCount,
    boolean isCustomized
) {
    private static final int DEFAULT_FOCUS_MINUTES = 25;
    private static final int DEFAULT_BREAK_MINUTES = 5;
    private static final int DEFAULT_CYCLE_COUNT = 4;

    public static TimerSettingResponse from(TimerSetting timerSetting) {
        return new TimerSettingResponse(
            BeverageResponse.from(timerSetting.getBeverage()),
            timerSetting.getFocusMinutes(),
            timerSetting.getBreakMinutes(),
            timerSetting.getCycleCount(),
            true
        );
    }

    public static TimerSettingResponse defaultResponse() {
        return new TimerSettingResponse(
            null,
            DEFAULT_FOCUS_MINUTES,
            DEFAULT_BREAK_MINUTES,
            DEFAULT_CYCLE_COUNT,
            false
        );
    }
}