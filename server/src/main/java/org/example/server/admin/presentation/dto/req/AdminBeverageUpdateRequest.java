package org.example.server.admin.presentation.dto.req;

import java.time.OffsetDateTime;

public record AdminBeverageUpdateRequest(
    String name,
    String imgUrl,
    Long price,
    String saleStatus,
    OffsetDateTime saleEndsAt,
    Integer displayOrder
) {
    public AdminBeverageUpdateRequest {
        name = trim(name);
        imgUrl = trim(imgUrl);
        saleStatus = trim(saleStatus);
    }

    private static String trim(String value) {
        return value == null ? null : value.trim();
    }
}
