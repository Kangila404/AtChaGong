package org.example.server.admin.presentation.dto.req;

import jakarta.validation.constraints.NotNull;
import org.example.server.user.domain.enums.UserStatus;

public record AdminUpdateUserStatusRequest(
    @NotNull(message = "변경할 사용자 상태는 필수입니다.")
    UserStatus userStatus
) {

}
