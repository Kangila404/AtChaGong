package org.example.server.notification.domain.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.server.common.entity.BaseEntity;
import org.example.server.notification.domain.enums.DeviceType;

@Getter
@Entity
@Table(name = "device_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceType platform;

    @Column(nullable = false)
    private boolean active;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    private DeviceToken(Long userId, String token, DeviceType platform, LocalDateTime lastUsedAt) {
        this.userId = userId;
        this.token = token;
        this.platform = platform;
        this.active = true;
        this.lastUsedAt = lastUsedAt;
    }

    public static DeviceToken register(Long userId, String token, DeviceType platform, LocalDateTime lastUsedAt) {
        return new DeviceToken(userId, token, platform, lastUsedAt);
    }

    public void reactivate(Long userId, DeviceType platform, LocalDateTime lastUsedAt) {
        this.userId = userId;
        this.platform = platform;
        this.active = true;
        this.lastUsedAt = lastUsedAt;
    }

    public void deactivate() {
        this.active = false;
    }
}
