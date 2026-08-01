package org.example.server.timer.infrastructure.persistence.repository;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.server.timer.domain.models.TimerSetting;
import org.example.server.timer.domain.repository.TimerSettingRepository;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TimerSettingRepositoryImpl implements TimerSettingRepository {

    private final TimerSettingJpaRepository timerSettingJpaRepository;

    @Override
    public Optional<TimerSetting> findByUserId(Long userId) {
        return null;
    }
}
