package io.ziggy.yfinance.service;

import io.ziggy.yfinance.api.FundamentalsApi;
import io.ziggy.yfinance.enums.Frequency;
import io.ziggy.yfinance.enums.StatementType;
import io.ziggy.yfinance.mapper.FundamentalsMapper;
import io.ziggy.yfinance.model.FinancialStatement;
import io.ziggy.yfinance.valueobject.Symbol;
import java.time.Clock;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.stream.Collectors;

/** Retrieves income, balance-sheet and cash-flow statements via the timeseries endpoint. */
public final class FundamentalsService {

    // Yahoo caps at ~4 years / 5 quarters regardless; this lower bound mirrors yfinance.
    private static final long PERIOD_START = LocalDate.of(2016, 12, 31).atStartOfDay(ZoneOffset.UTC).toEpochSecond();

    private final FundamentalsApi api;
    private final Clock clock;

    public FundamentalsService(FundamentalsApi api) {
        this(api, Clock.systemUTC());
    }

    public FundamentalsService(FundamentalsApi api, Clock clock) {
        this.api = Objects.requireNonNull(api, "api");
        this.clock = Objects.requireNonNull(clock, "clock");
    }

    public FinancialStatement getStatement(Symbol symbol, StatementType type, Frequency frequency) {
        String typeParam = FundamentalKeys.forStatement(type).stream()
                .map(key -> frequency.wireValue() + key)
                .collect(Collectors.joining(","));
        long now = clock.instant().getEpochSecond();
        var response = api.timeseries(symbol.value(), typeParam, PERIOD_START, now);
        return FundamentalsMapper.toStatement(response, type, frequency);
    }
}
