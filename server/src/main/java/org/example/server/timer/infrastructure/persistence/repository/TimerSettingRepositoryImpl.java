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
        return timerSettingJpaRepository.findByUserId(userId);
    }

    @Override
    public TimerSetting save(TimerSetting timerSetting) {
        return timerSettingJpaRepository.save(timerSetting);
    }

    @Override
    public void deleteByUserId(Long userId) {
        timerSettingJpaRepository.deleteByUserId(userId);
    }
}
