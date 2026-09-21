package org.example.server.notification.application;

public interface PushNotificationSender {
    void send(String token, String title, String body);
}
