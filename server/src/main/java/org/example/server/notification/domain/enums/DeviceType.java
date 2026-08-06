package org.example.server.notification.domain.enums;

public enum DeviceType {
    ANDROID,
    IOS;

    public static DeviceType from(String value) {
        if (value == null) {
            throw new IllegalArgumentException("플랫폼이 올바르지 않습니다.");
        }

        try {
            return DeviceType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("플랫폼이 올바르지 않습니다.");
        }
    }

    public String toResponseValue() {
        return name().toLowerCase();
    }
}
