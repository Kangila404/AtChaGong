package org.example.server.admin.presentation.dto.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record AdminNoticePageRequest(
    @Min(0)
    Integer page,

    @Min(1)
    @Max(AdminNoticePageRequest.MAX_SIZE)
    Integer size,

    String status
) {
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;
    public static final String DEFAULT_STATUS = "all";

    public AdminNoticePageRequest {
        page = page == null ? DEFAULT_PAGE : page;
        size = size == null ? DEFAULT_SIZE : size;
        status = status == null || status.isBlank() ? DEFAULT_STATUS : status.trim();
    }
}
