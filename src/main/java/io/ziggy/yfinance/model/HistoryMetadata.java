package io.ziggy.yfinance.model;

import io.ziggy.yfinance.valueobject.Symbol;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Currency;

/** Instrument metadata accompanying a price-history response. */
public record HistoryMetadata(
        Symbol symbol,
        Currency currency,
        String exchangeName,
        String fullExchangeName,
        String instrumentType,
        ZoneId timezone,
        Instant firstTradeDate,
        BigDecimal regularMarketPrice,
        BigDecimal previousClose) {}
