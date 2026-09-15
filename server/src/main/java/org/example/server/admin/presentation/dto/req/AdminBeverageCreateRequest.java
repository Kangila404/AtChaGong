package org.example.server.admin.presentation.dto.req;

import java.time.OffsetDateTime;

public record AdminBeverageCreateRequest(
    String name,
    String imgUrl,
    Long price,
    Integer displayOrder,
    OffsetDateTime saleEndsAt
) {
    public AdminBeverageCreateRequest {
        name = trim(name);
        imgUrl = trim(imgUrl);
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
