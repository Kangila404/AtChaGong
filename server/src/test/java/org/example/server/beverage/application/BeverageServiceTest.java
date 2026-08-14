package org.example.server.beverage.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.List;
import org.example.server.beverage.domain.models.Beverage;
import org.example.server.beverage.domain.repository.BeverageRepository;
import org.example.server.beverage.presentation.dto.res.BeverageResponse;
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
        Beverage americano = beverage(1L, "americano", "https://example.com/americano.png");
        Beverage latte = beverage(2L, "latte", "https://example.com/latte.png");
        given(beverageRepository.findAll()).willReturn(List.of(americano, latte));

        List<BeverageResponse> response = beverageService.getBeverages();

        assertThat(response).hasSize(2);
        assertThat(response.get(0).beverageId()).isEqualTo(1L);
        assertThat(response.get(0).name()).isEqualTo("americano");
        assertThat(response.get(0).imgUrl()).isEqualTo("https://example.com/americano.png");
        assertThat(response.get(1).beverageId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("음료가 없으면 빈 목록을 반환한다")
    void getBeveragesWithoutBeveragesReturnsEmptyList() {
        given(beverageRepository.findAll()).willReturn(List.of());

        List<BeverageResponse> response = beverageService.getBeverages();

        assertThat(response).isEmpty();
    }

    private Beverage beverage(Long id, String name, String imgUrl) {
        Beverage beverage = org.mockito.Mockito.mock(Beverage.class);
        given(beverage.getId()).willReturn(id);
        given(beverage.getName()).willReturn(name);
        given(beverage.getImgUrl()).willReturn(imgUrl);
        return beverage;
    }
}
