package io.ziggy.yfinance.service;

import io.ziggy.yfinance.api.LookupApi;
import io.ziggy.yfinance.enums.LookupType;
import io.ziggy.yfinance.mapper.LookupMapper;
import io.ziggy.yfinance.model.LookupQuote;
import java.util.List;
import java.util.Objects;

/** Looks up instruments by name or symbol fragment. */
public final class LookupService {

    private static final int DEFAULT_COUNT = 25;

    private final LookupApi api;

    public LookupService(LookupApi api) {
        this.api = Objects.requireNonNull(api, "api");
    }

    public List<LookupQuote> lookup(String query, LookupType type) {
        return lookup(query, type, DEFAULT_COUNT);
    }

    public List<LookupQuote> lookup(String query, LookupType type, int count) {
        var response = api.lookup(query, type.wireValue(), 0, count, false, true);
        return LookupMapper.toQuotes(response);
    }
}
