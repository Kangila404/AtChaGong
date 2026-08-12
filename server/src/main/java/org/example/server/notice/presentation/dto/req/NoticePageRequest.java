package org.example.server.notice.presentation.dto.req;

public record NoticePageRequest(
    String page,

    String size
) {
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;
}
