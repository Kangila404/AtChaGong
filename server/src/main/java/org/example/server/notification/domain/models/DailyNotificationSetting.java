package org.example.server.notification.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.server.common.entity.BaseEntity;

@Getter
@Entity
@Table(name = "daily_notification_setting")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyNotificationSetting extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;
    @Column(name = "notification_time", nullable = false)
    private LocalTime notificationTime;
    @Column(nullable = false)
    private boolean enabled;

    private DailyNotificationSetting(Long userId, LocalTime notificationTime, boolean enabled) {
        this.userId = userId;
        this.notificationTime = notificationTime;
        this.enabled = enabled;
    }
    public static DailyNotificationSetting create(Long userId, LocalTime notificationTime, boolean enabled) {
        return new DailyNotificationSetting(userId, notificationTime, enabled);
    }
    public void update(LocalTime notificationTime, boolean enabled) {
        this.notificationTime = notificationTime;
        this.enabled = enabled;
    }
}
