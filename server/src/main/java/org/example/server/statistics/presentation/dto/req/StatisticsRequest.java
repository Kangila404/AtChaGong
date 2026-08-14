package org.example.server.statistics.presentation.dto.req;

import org.example.server.statistics.domain.enums.StatisticsPeriod;

public record StatisticsRequest(
    StatisticsPeriod period
) {

}
