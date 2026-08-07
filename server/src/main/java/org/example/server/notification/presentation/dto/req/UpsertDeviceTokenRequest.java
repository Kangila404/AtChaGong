package org.example.server.notification.presentation.dto.req;

public record UpsertDeviceTokenRequest(
    String token,
    String platform
) {
}
