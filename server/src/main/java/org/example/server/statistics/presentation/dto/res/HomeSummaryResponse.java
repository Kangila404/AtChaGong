package org.example.server.statistics.presentation.dto.res;

import org.example.server.beverage.domain.models.Beverage;
import org.example.server.timer.domain.models.TimerSetting;
import org.example.server.user.domain.models.User;

public record HomeSummaryResponse(
    UserSumary user,
    TimerSettingSumary timerSetting,
    BeverageSummary defaultBeverage,
    TodayStats todayStats

) {

    public static HomeSummaryResponse of(
        User user,
        TimerSetting timerSetting,
        int currentCycle,
        Beverage defaultBeverage,
        long focusedSeconds,
        int completedCupCount,
        int completedCycleCount,
        boolean cycleAchieved
    ) {
        return new HomeSummaryResponse(
            new UserSumary(user.getNickname()),
            new TimerSettingSumary(
                timerSetting.getFocusMinutes(),
                timerSetting.getBreakMinutes(),
                timerSetting.getCycleCount(),
                currentCycle
            ),
            defaultBeverage == null
                ? null
                : new BeverageSummary(
                    defaultBeverage.getId(),
                    defaultBeverage.getName(),
                    defaultBeverage.getImgUrl()
                ),
            new TodayStats(
                focusedSeconds,
                completedCupCount,
                completedCycleCount,
                cycleAchieved
            )
        );
    }


    // ========== 하위 dto ========== //
    public record UserSumary(String nickname) {

    }

    public record TimerSettingSumary(
        int focusMinutes,
        int breakMinutes,
        int cycleCount,
        int currentCycle
    ) {

    }

    public record BeverageSummary(
        Long beverageId,
        String name,
        String imgUrl
    ) {

    }

    public record TodayStats(
        long focusedSeconds,
        int completedCupCount,
        int completedCycleCount,
        boolean cycleAchieved
    ) {

    }
}
