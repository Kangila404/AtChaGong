package org.example.server.auth.presentation.dto.res;

public record DevSignupResponse(
    String userId
) {

    public static DevSignupResponse from(String userId) {
        return new DevSignupResponse(userId);
    }
}