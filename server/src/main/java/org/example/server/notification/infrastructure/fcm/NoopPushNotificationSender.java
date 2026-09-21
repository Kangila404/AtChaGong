package org.example.server.notification.infrastructure.fcm;

import lombok.extern.slf4j.Slf4j;
import org.example.server.notification.application.PushNotificationSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "fcm.credentials-path", havingValue = "", matchIfMissing = true)
public class NoopPushNotificationSender implements PushNotificationSender {
    @Override
    public void send(String token, String title, String body) {
        log.warn("FCM credentials are not configured; notification was skipped");
    }
}
