package io.ziggy.yfinance.service;

import io.ziggy.yfinance.api.ChartApi;
import io.ziggy.yfinance.enums.EventType;
import io.ziggy.yfinance.mapper.ChartMapper;
import io.ziggy.yfinance.model.PriceHistory;
import java.util.Objects;
import java.util.stream.Collectors;

/** Retrieves and maps price history from the chart endpoint. */
public final class HistoryService {

    private final ChartApi api;

    public HistoryService(ChartApi api) {
        this.api = Objects.requireNonNull(api, "api");
    }

    public PriceHistory getHistory(HistoryRequest request) {
        String events = request.events().stream()
                .map(EventType::wireValue)
                .collect(Collectors.joining(","));
        var response = api.chart(
                request.symbol().value(),
                request.interval().wireValue(),
                request.hasPeriod() ? null : request.range().wireValue(),
                request.hasPeriod() ? request.start().getEpochSecond() : null,
                request.hasPeriod() && request.end() != null ? request.end().getEpochSecond() : null,
                request.includePrePost(),
                events.isEmpty() ? null : events);
        return ChartMapper.toPriceHistory(response, request.symbol());
    }
}
