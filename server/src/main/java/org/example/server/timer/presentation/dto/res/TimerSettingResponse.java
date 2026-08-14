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
            TimerSetting.DEFAULT_FOCUS_MINUTES,
            TimerSetting.DEFAULT_BREAK_MINUTES,
            TimerSetting.DEFAULT_CYCLE_COUNT,
            false
        );
    }
}