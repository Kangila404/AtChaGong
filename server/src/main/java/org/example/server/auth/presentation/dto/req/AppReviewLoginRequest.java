package org.example.server.auth.presentation.dto.req;

import jakarta.validation.constraints.NotBlank;

public record AppReviewLoginRequest(
    @NotBlank String loginId,
    @NotBlank String password
) {
}
