package org.example.server.admin.presentation.dto.req;

public record AdminNoticePageRequest(
    String page,

    String size,

    String status
) {
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;
    public static final String DEFAULT_STATUS = "all";

    public AdminNoticePageRequest {
        status = status == null || status.isBlank() ? DEFAULT_STATUS : status.trim();
    }
}
