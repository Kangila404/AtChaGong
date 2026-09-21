package org.example.server.beverage.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.example.server.beverage.domain.models.Beverage;
import org.example.server.beverage.domain.repository.BeverageRepository;
import org.example.server.beverage.presentation.dto.res.BeverageSaleResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class BeverageServiceTest {

    @InjectMocks
    private BeverageService beverageService;

    @Mock
    private BeverageRepository beverageRepository;

    @Test
    @DisplayName("음료 목록을 응답 DTO로 변환해서 반환한다")
    void getBeveragesReturnsBeverageResponses() {
        Beverage americano = beverage(
            1L,
            "americano",
            0L,
            null
        );
        Beverage latte = beverage(
            2L,
            "latte",
            500L,
            LocalDateTime.of(2026, 11, 1, 0, 0)
        );
        given(beverageRepository.findAllAvailableForSale(any(LocalDateTime.class)))
            .willReturn(List.of(americano, latte));

        List<BeverageSaleResponse> response = beverageService.getBeverages();

        assertThat(response)
            .extracting(
                BeverageSaleResponse::beverageId,
                BeverageSaleResponse::name,
                BeverageSaleResponse::price,
                BeverageSaleResponse::isLimited,
                BeverageSaleResponse::saleEndsAt
            )
            .containsExactly(
                tuple(1L, "americano", 0L, false, null),
                tuple(
                    2L,
                    "latte",
                    500L,
                    true,
                    LocalDateTime.of(2026, 11, 1, 0, 0).atOffset(ZoneOffset.ofHours(9))
                )
            );
    }

    @Test
    @DisplayName("음료가 없으면 빈 목록을 반환한다")
    void getBeveragesWithoutBeveragesReturnsEmptyList() {
        given(beverageRepository.findAllAvailableForSale(any(LocalDateTime.class)))
            .willReturn(List.of());

        List<BeverageSaleResponse> response = beverageService.getBeverages();

        assertThat(response).isEmpty();
    }

    private Beverage beverage(
        Long id,
        String name,
        Long price,
        LocalDateTime saleEndsAt
    ) {
        Beverage beverage = org.mockito.Mockito.mock(Beverage.class);
        given(beverage.getId()).willReturn(id);
        given(beverage.getName()).willReturn(name);
        given(beverage.getPrice()).willReturn(price);
        given(beverage.getSaleEndsAt()).willReturn(saleEndsAt);
        given(beverage.isLimited()).willReturn(saleEndsAt != null);
        return beverage;
    }
}
