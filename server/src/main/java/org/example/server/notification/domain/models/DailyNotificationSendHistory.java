package org.example.server.notification.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.server.common.entity.BaseEntity;

@Getter
@Entity
@Table(name = "daily_notification_send_history")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyNotificationSendHistory extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "notification_date", nullable = false)
    private LocalDate notificationDate;
    private DailyNotificationSendHistory(Long userId, LocalDate notificationDate) {
        this.userId = userId;
        this.notificationDate = notificationDate;
    }
    public static DailyNotificationSendHistory create(Long userId, LocalDate notificationDate) {
        return new DailyNotificationSendHistory(userId, notificationDate);
    }
}
