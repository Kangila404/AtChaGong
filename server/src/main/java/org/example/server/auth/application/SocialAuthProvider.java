package org.example.server.auth.application;

import org.example.server.auth.domain.enums.AuthType;
import org.example.server.auth.presentation.dto.SocialUserInfo;

public interface SocialAuthProvider {
    AuthType supports();
    SocialUserInfo verify(String credential);
}
