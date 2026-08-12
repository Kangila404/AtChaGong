package org.example.server.notice.domain.enums;

import java.util.Arrays;

public enum NoticeStatus {
    PUBLISHED,
    ENDED;

    public static NoticeStatus from(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("공지 상태가 올바르지 않습니다.");
        }

        return Arrays.stream(values())
            .filter(status -> status.name().equalsIgnoreCase(value.trim()))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("공지 상태가 올바르지 않습니다."));
    }

    public String value() {
        return name().toLowerCase();
    }
}
