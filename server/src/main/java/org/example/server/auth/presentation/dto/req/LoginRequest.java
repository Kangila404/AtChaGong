package org.example.server.auth.presentation.dto.req;

import org.example.server.auth.domain.enums.AuthType;
import org.example.server.auth.presentation.dto.res.LoginResponse;

public record LoginRequest(
    AuthType authType,
    String credential
) {

}
