package org.example.server.notification.infrastructure.fcm;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import java.io.FileInputStream;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.example.server.notification.application.PushNotificationSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Primary
@ConditionalOnProperty(name = "fcm.credentials-path")
public class FcmPushNotificationSender implements PushNotificationSender {
    public FcmPushNotificationSender(@Value("${fcm.credentials-path}") String credentialsPath) throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(new FileInputStream(credentialsPath)))
                .build();
            FirebaseApp.initializeApp(options);
        }
    }

    @Override
    public void send(String token, String title, String body) {
        Message message = Message.builder()
            .setToken(token)
            .setNotification(Notification.builder().setTitle(title).setBody(body).build())
            .build();
        try {
            FirebaseMessaging.getInstance().send(message);
        } catch (Exception exception) {
            log.warn("Failed to send FCM notification", exception);
        }
    }
}
