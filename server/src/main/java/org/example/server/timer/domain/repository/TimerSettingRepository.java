package org.example.server.timer.domain.repository;

import java.util.Optional;
import org.example.server.timer.domain.models.TimerSetting;

public interface TimerSettingRepository {
    Optional<TimerSetting> findByUserId(Long userId);
    TimerSetting save(TimerSetting timerSetting);
}
