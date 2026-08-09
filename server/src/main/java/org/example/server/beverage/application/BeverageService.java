package org.example.server.beverage.application;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.server.beverage.domain.models.Beverage;
import org.example.server.beverage.domain.repository.BeverageRepository;
import org.example.server.beverage.presentation.dto.res.BeverageResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BeverageService {

    private final BeverageRepository beverageRepository;

    @Transactional(readOnly = true)
    public List<BeverageResponse> getBeverages(){
        List<Beverage> beverages = beverageRepository.findAll();

        return beverages.stream()
            .map(BeverageResponse::from)
            .toList();
    }

}
