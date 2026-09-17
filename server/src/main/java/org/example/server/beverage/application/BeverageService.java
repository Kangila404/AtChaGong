package org.example.server.beverage.application;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.server.beverage.domain.models.Beverage;
import org.example.server.beverage.domain.repository.BeverageRepository;
import org.example.server.beverage.presentation.dto.res.BeverageSaleResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BeverageService {

    private static final ZoneId SEOUL_ZONE = ZoneId.of("Asia/Seoul");

    private final BeverageRepository beverageRepository;

    @Transactional(readOnly = true)
    public List<BeverageSaleResponse> getBeverages() {
        LocalDateTime now = LocalDateTime.now(SEOUL_ZONE);
        List<Beverage> beverages = beverageRepository.findAllAvailableForSale(now);

        return beverages.stream()
            .map(BeverageSaleResponse::from)
            .toList();
    }

}
