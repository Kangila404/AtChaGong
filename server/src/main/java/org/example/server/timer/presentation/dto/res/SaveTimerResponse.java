package org.example.server.timer.presentation.dto.res;

import org.example.server.beverage.presentation.dto.res.BeverageResponse;
import org.example.server.timer.domain.models.TimerSetting;

public record SaveTimerResponse(
    BeverageResponse beverage,
    int focusMinutes,
    int breakMinutes,
    int cycleCount
) {
    public static SaveTimerResponse from(TimerSetting timerSetting) {
        return new SaveTimerResponse(
            BeverageResponse.from(timerSetting.getBeverage()),
            timerSetting.getFocusMinutes(),
            timerSetting.getBreakMinutes(),
            timerSetting.getCycleCount()
        );
    }
}