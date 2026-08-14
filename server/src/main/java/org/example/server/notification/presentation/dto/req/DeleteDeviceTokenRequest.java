package org.example.server.notification.presentation.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DeleteDeviceTokenRequest(
    @NotBlank
    @Size(max = 512)
    String token
) {
}
