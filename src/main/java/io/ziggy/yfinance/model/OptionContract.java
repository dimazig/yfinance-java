package io.ziggy.yfinance.model;

import io.ziggy.yfinance.enums.OptionType;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Currency;

/** A single option contract (call or put). */
public record OptionContract(
        String contractSymbol,
        OptionType type,
        BigDecimal strike,
        Currency currency,
        BigDecimal lastPrice,
        BigDecimal bid,
        BigDecimal ask,
        BigDecimal change,
        BigDecimal percentChange,
        Long volume,
        Long openInterest,
        BigDecimal impliedVolatility,
        boolean inTheMoney,
        String contractSize,
        Instant lastTradeDate,
        Instant expiration) {}
