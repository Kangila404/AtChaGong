package org.example.server.notification.infrastructure.persistence.repository;

import java.time.LocalDate;
import org.example.server.notification.domain.models.DailyNotificationSendHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyNotificationSendHistoryJpaRepository extends JpaRepository<DailyNotificationSendHistory, Long> {
    boolean existsByUserIdAndNotificationDate(Long userId, LocalDate notificationDate);
}
