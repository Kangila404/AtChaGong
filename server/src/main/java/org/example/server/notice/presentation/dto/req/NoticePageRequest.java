package org.example.server.notice.presentation.dto.req;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record NoticePageRequest(
    @Min(0)
    Integer page,

    @Min(1)
    @Max(NoticePageRequest.MAX_SIZE)
    Integer size
) {
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;

    public NoticePageRequest {
        page = page == null ? DEFAULT_PAGE : page;
        size = size == null ? DEFAULT_SIZE : size;
    }
}
