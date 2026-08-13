package org.example.server.user.presentation.dto.req;

import jakarta.validation.constraints.NotNull;

public record UpdateProfileImgRequest(
    @NotNull
    Long profileId
) {

}
