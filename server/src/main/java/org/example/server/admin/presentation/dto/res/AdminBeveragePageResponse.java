package org.example.server.admin.presentation.dto.res;

import java.util.List;
import lombok.Builder;
import org.example.server.beverage.domain.models.Beverage;
import org.springframework.data.domain.Page;

@Builder
public record AdminBeveragePageResponse(
    List<AdminBeverageResponse> content,
    int page,
    int size,
    long totalElements,
    int totalPages
) {
    public static AdminBeveragePageResponse of(Page<Beverage> page, List<AdminBeverageResponse> content) {
        return AdminBeveragePageResponse.builder()
            .content(content)
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .build();
    }
}
