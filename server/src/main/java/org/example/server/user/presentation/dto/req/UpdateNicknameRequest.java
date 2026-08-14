package org.example.server.user.presentation.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateNicknameRequest(
    @NotBlank(message = "닉네임에는 공백이 올 수 없습니다.")
    @Size(min = 1, max = 20, message = "닉네임은 1~20자여야 합니다.")
    @Pattern(regexp = "^\\S+$", message = "닉네임에 공백을 포함할 수 없습니다.")
    String nickname
) {}