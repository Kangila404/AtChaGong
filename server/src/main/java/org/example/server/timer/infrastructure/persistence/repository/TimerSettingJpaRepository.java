package org.example.server.timer.infrastructure.persistence.repository;

import java.util.Optional;
import org.example.server.timer.domain.models.TimerSetting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimerSettingJpaRepository extends JpaRepository<TimerSetting,Long> {

    Optional<TimerSetting> findByUserId(Long userId);
}
