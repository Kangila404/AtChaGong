package org.example.server.admin.presentation.dto.req;

public record AdminCoinTransactionPageRequest(
    String page,
    String size
) {
    public static final int DEFAULT_PAGE = 0;
    public static final int DEFAULT_SIZE = 20;
    public static final int MAX_SIZE = 100;
}
