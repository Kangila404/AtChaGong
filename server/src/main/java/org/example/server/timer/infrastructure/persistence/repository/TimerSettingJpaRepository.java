package org.example.server.timer.infrastructure.persistence.repository;

import java.util.Optional;
import org.example.server.timer.domain.models.TimerSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface TimerSettingJpaRepository extends JpaRepository<TimerSetting,Long> {

    Optional<TimerSetting> findByUserId(Long userId);

    @Modifying
    @Query("delete from TimerSetting timerSetting where timerSetting.userId = :userId")
    void deleteByUserId(Long userId);
}
